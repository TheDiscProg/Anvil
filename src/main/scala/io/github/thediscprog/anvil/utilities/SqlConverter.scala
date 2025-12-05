package io.github.thediscprog.anvil.utilities

import java.time.LocalDate
import java.sql.Date
import java.time.LocalTime
import java.sql.Time
import java.time.LocalDateTime
import java.sql.Timestamp

object SqlConverter {

  def convertDateToSql(date: LocalDate): Date = Date.valueOf(date)

  def convertDateFromSql(date: Date): LocalDate = date.toLocalDate()

  def convertTimeToSql(time: LocalTime): Time = Time.valueOf(time)

  def convertTimeFromSql(time: Time): LocalTime = time.toLocalTime()

  def convertTimestampToSql(timestamp: LocalDateTime): Timestamp =
    Timestamp.valueOf(timestamp)

  def convertTimestampFromSql(timestame: Timestamp): LocalDateTime =
    timestame.toLocalDateTime()
}
