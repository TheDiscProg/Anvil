package io.github.thediscprog.anvil.adt

import cats.Monad
import cats.effect.{Resource, Sync}
import cats.implicits.*
import io.github.thediscprog.anvil.annotations.PrimaryKeyAnnotation
import io.github.thediscprog.anvil.caching.{CaffeineMemoize, Memoize}
import io.github.thediscprog.anvil.dialects.SqlDialect
import io.github.thediscprog.anvil.exceptions.AnvilException.MappingError
import io.github.thediscprog.anvil.exceptions.AnvilMessageKey
import io.github.thediscprog.anvil.i18n.AnvilMessage
import io.github.thediscprog.anvil.jdbcutils.*
import io.github.thediscprog.anvil.macros.ProductMacro.*
import io.github.thediscprog.anvil.monitor.AnvilMonitor
import org.typelevel.log4cats.Logger
import java.sql.{Array as _, *}
import javax.sql.DataSource
import scala.annotation.nowarn
import scala.deriving.Mirror

trait TableMapping[F[_], A <: Product](
    val properties: TableProperties
) {

  def withConnection[B](f: Connection => F[B]): F[B]

  def transactional[R](f: () => F[R]): F[R]

  infix def /~>(criteria: Criteria): F[List[A]] = filter(criteria)

  infix def /->(criteria: Criteria): F[List[A]] = filter(criteria, true)

  infix def /?(criteria: Criteria): F[Option[A]] = headOption(criteria)

  infix def <+(a: A): F[Int] = add(a)

  infix def <--(criteria: Criteria): F[Int] = deleteWhere(criteria)

  infix def filterDistinct(criteria: Criteria): F[List[A]] =
    filter(criteria, distinct = true)

  infix def filter(criteria: Criteria, distinct: Boolean = false): F[List[A]]

  infix def headOption(criteria: Criteria): F[Option[A]]

  infix def add(a: A): F[Int]

  infix def updateWhere(criteria: Criteria)(
      updateWith: List[KeyValue[Any]]
  ): F[Int]

  infix def deleteWhere(criteria: Criteria): F[Int]

}

object TableMapping {
  import JDBCBinder.*

  @nowarn
  inline def getFRM[F[_]: Monad: Logger: Sync, A <: Product](
      tableProps: TableProperties,
      dataSource: DataSource,
      reader: JDBCReaderSelector = new JDBCReaderSelector()
  )(using m: Mirror.ProductOf[A]): TableMapping[F, A] = {

    new TableMapping[F, A](tableProps) {

      given monitor: AnvilMonitor = AnvilMonitor.getMonitor(tableProps.table)

      val dialect = SqlDialect.getDialect(tableProps.dialect)

      val metaDataCache               = s"${tableProps.cachingKey}-rsmd"
      val pkCache                     = s"${tableProps.cachingKey}-pk"
      val columnDescriptorCache       = s"${tableProps.cachingKey}-cds"
      val memoize: CaffeineMemoize[F] = Memoize.getMemoizeFunction[F]

      val primaryKey = PrimaryKeyAnnotation.getPrimaryKeyNames[A]

      val pkMap: List[(String, String)] = getPrimaryKeyMapping()

      val fieldMap = getFieldLabelAndTypes[A]

      override def transactional[R](f: () => F[R]): F[R] = {
        this.withConnection { connection =>
          {
            for
              _ <- Logger[F].debug(
                s"* * * TableMapping: Running Transactional on ${properties.table} using ${properties.dialect} * * *"
              )
              autoCommitFlag = connection.getAutoCommit()
              _              = connection.setAutoCommit(false)
              result <- f()
              _ = connection.commit()
              _ = connection.setAutoCommit(autoCommitFlag)
              _ <- Logger[F].debug(
                s"* * * TableMapping: Finished Transactional on ${properties.table} using ${properties.dialect} * * *"
              )
            yield result
          }
        }
      }

      override def add(a: A): F[Int] = {
        this.withConnection { connection =>
          {
            val columns    = getColumnLabels(excludePK = true)
            val values     = getProductValues(a)
            val insertStmt = dialect.insert(columns, tableProps.table, values)
            for
              _ <- Logger[F].debug(
                s"* * * TableMapping: Insert on ${properties.table} using ${properties.dialect} * * *"
              )
              _ <- Logger[F].info(
                s"TableMapping - table: ${properties.table}, INSERT:[$insertStmt]"
              )
              timer = monitor.startInsertTimer()
              stmt <- (connection.prepareStatement(insertStmt)).pure[F]
              _    <- (bindParameters(
                stmt,
                values.toList,
                connection,
                dialect
              ))
                .pure[F]
              result <- (stmt.executeUpdate()).pure[F]
              _ = monitor.stopTimer(timer)
              _ <- Logger[F].debug(s"* * * TableMapping: Insert Finished * * *")
              _ = monitor.insertCall()
            yield result
          }
        }
      }

      override def headOption(criteria: Criteria): F[Option[A]] = {
        for
          _ <- Logger[F].debug(
            s"* * * TableMapping: Running getOne on ${properties.table} using ${properties.dialect} * * *"
          )
          aList <- filter(criteria)
          result = aList match
            case head :: next => Some(head)
            case Nil          => None
          _ <- Logger[F].debug(
            s"* * * TableMapping: Finished running getOne on ${properties.table} * * *"
          )
        yield result
      }

      override def filter(
          criteria: Criteria,
          distinct: Boolean = false
      ): F[List[A]] = {
        this.withConnection { connection =>
          {
            for
              _ <- Logger[F].debug(
                s"* * * TableMapping: Filter on ${properties.table} using ${properties.dialect} * * *"
              )
              columns = getColumnLabels(excludePK = false)
              _ <- Logger[F].debug(s"TableMapping: Columns: [$columns]")
              whereClause     = Criteria.getWhereClause(criteria.criteria)
              selectStatement =
                if distinct then
                  dialect.filterDistinct(
                    columns,
                    properties.table,
                    whereClause._1
                  )
                else
                  dialect.filter(columns, properties.table, whereClause._1)
              _ <- Logger[F].info(
                s"* * * * TableMapping: Table: [${properties.table}], Query: [$selectStatement]"
              )
              statement: PreparedStatement = connection.prepareStatement(
                selectStatement
              )
              timer = monitor.startSelectTimer()
              _     = bindParameters(
                statement,
                whereClause._2,
                connection,
                dialect
              )
              metadata <- getResultSetMetaData(statement)
              resultSet         = statement.executeQuery()
              columnDescriptors = ColumnDescriptor.getColumnInformation(
                metadata,
                fieldMap,
                reader,
                dialect
              )
              _ = monitor.stopTimer(timer)
              _ <- Logger[F].debug(
                s"==== Column Descriptors: [${columnDescriptors}] ===="
              )
              result <- convertResultSet(columnDescriptors.toList, resultSet)
                .pure[F]
              _ <- Logger[F].debug(
                s"* * * TableMapping: Finished Filter on ${properties.table} * * *"
              )
              _ = monitor.selectCall()
            yield result
          }
        }
      }

      override def deleteWhere(criteria: Criteria): F[Int] = {
        this.withConnection { connection =>
          {
            for
              _ <- Logger[F].debug(
                s"* * * TableMapping: Deleting from ${properties.table} * * *"
              )
              whereClause = Criteria.getWhereClause(criteria.criteria)
              deleteStmt  = dialect.delete(tableProps.table, whereClause._1)
              _ <- Logger[F].info(
                s"* * * TableMapping: Table: [${properties.table}], Delete: [$deleteStmt] * * *"
              )
              preparedStmt <- (connection.prepareStatement(deleteStmt)).pure[F]
              timer = monitor.startDeleteTimer()
              _ <- bindParameters(
                preparedStmt,
                whereClause._2,
                connection,
                dialect
              )
                .pure[F]
              numberUpdated <- (preparedStmt.executeUpdate()).pure[F]
              _ = monitor.stopTimer(timer)
              _ <- Logger[F].debug(
                s"* * * TableMapping: Finished Deleting from ${properties.table} * * *"
              )
              _ = monitor.deleteCall()
            yield numberUpdated
          }
        }
      }

      override def updateWhere(
          criteria: Criteria
      )(updateWith: List[KeyValue[Any]]): F[Int] = {
        this.withConnection { connection =>
          {
            for
              _ <- Logger[F].debug(
                s"* * * TableMapping: Updating ${properties.table} * * *"
              )
              updateColumns: String = setUpdateColumns(updateWith)
              newValues             = updateWith.map(_.value)
              whereClause           = Criteria.getWhereClause(criteria.criteria)
              updateStmt            = dialect.update(
                updateColumns,
                properties.table,
                whereClause._1
              )
              _ <- Logger[F].info(
                s"* * * TableMapping: Table: [${properties.table}], Update: [$updateStmt] * * *"
              )
              preparedStmt <- (connection.prepareStatement(updateStmt)).pure[F]
              timer = monitor.startUpdateTimer()
              _ <- (bindParameters(
                preparedStmt,
                newValues ++ whereClause._2,
                connection,
                dialect
              )).pure[F]
              result <- (preparedStmt.executeUpdate()).pure[F]
              _ = monitor.stopTimer(timer)
              _ <- Logger[F].debug(
                s"* * * TableMapping: FinishedUpdating ${properties.table} * * *"
              )
              _ = monitor.updateCall()
            yield result
          }
        }
      }

      private def getResultSetMetaData(
          sttm: PreparedStatement
      ): F[ResultSetMetaData] = {
        memoize.memoize[ResultSetMetaData](properties.cachingKey)(_ =>
          sttm.getMetaData().pure[F]
        )
      }

      private def setUpdateColumns(updates: List[KeyValue[Any]]): String = {
        updates
          .map { kv =>
            s"${kv.key}=?"
          }
          .mkString(",")
      }

      private def getProductValues(a: A): Array[Any] = {
        if primaryKey.autoGenerated then {
          val values        = getProductValuesWithoutPK(a)
          val valuesToPrint = values.mkString(",")
          values
        } else {
          a.productIterator.toArray
        }
      }

      private def getProductValuesWithoutPK(a: A): Array[Any] = {
        val names  = fieldNamesOf[A]
        val values = a.productIterator.toList
        names
          .zip(values)
          .filterNot { case (name, _) => primaryKey.columnNames.contains(name) }
          .map(_._2)
          .toArray
      }

      private def convertResultSet(
          columnDescriptors: List[ColumnDescriptor],
          resultSet: ResultSet
      ): List[A] = {
        def loop(acc: List[A], rs: ResultSet): List[A] = {
          if !rs.next() then acc
          else {
            val row = mapToScala(columnDescriptors, rs)
            loop(row :: acc, rs)
          }
        }
        loop(List(), resultSet)
      }

      private def mapToScala(cds: List[ColumnDescriptor], rs: ResultSet)(using
          m: Mirror.ProductOf[A]
      ): A = {
        val values = cds.map { cd =>
          readValueFromResultSet(cd, rs)
        }
        m.fromProduct(Tuple.fromArray(values.toArray))
      }

      private def readValueFromResultSet(
          cd: ColumnDescriptor,
          rs: ResultSet
      ): Any = {
        if cd.dataType != Types.ARRAY then {
          if cd.nullable then {
            cd.reader.readNullable(cd.index, rs)
          } else {
            cd.reader.readFromDb(cd.index, rs)
          }
        } else {
          cd.reader.readArray(
            cd.columnName,
            cd.index,
            cd.nullable,
            rs,
            reader,
            dialect
          )
        }
      }

      private def getColumnLabels[A](excludePK: Boolean): String = {
        val cols: Seq[String] = {
          if properties.columnNames.isEmpty then {
            getColumnNames[m.MirroredElemLabels]
          } else {
            properties.columnNames
          }
        }
        val columnNames = if excludePK && primaryKey.autoGenerated then {
          cols.filterNot(col => pkMap.map(_._2).contains(col))
        } else {
          cols
        }
        val columns = if properties.isNamingSpecial then {
          columnNames
            .map(col =>
              s"${dialect.specialIdentifierCharacter}$col${dialect.specialIdentifierCharacter}"
            )
            .mkString(",")
        } else {
          columnNames.mkString(",")
        }
        columns
      }

      private def getPrimaryKeyMapping(): List[(String, String)] = {
        if tableProps.columnNames.isEmpty then {
          primaryKey.columnNames.map(pk => (pk, pk))
        } else {
          val caseClassLabels = getColumnNames[m.MirroredElemLabels]
          val pkIndexes       = primaryKey.columnNames.map(column =>
            caseClassLabels.indexOf(column)
          )
          if caseClassLabels.size != tableProps.columnNames.size then {
            monitor.mismatchPrimaryKeyError()
            throw MappingError(
              AnvilMessage(AnvilMessageKey.ColumnMismatchError)
            )
          }
          val zippedLabels = caseClassLabels.zip(tableProps.columnNames)
          pkIndexes.map(i => zippedLabels(i))
        }
      }

      override def withConnection[B](f: Connection => F[B]): F[B] =
        connectionResource.use(f)

      private def connectionResource: Resource[F, Connection] =
        Resource.fromAutoCloseable(
          Sync[F].blocking(dataSource.getConnection())
        )
    }
  }
}
