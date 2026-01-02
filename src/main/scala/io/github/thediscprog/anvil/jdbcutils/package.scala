package io.github.thediscprog.anvil

import java.util.UUID
import java.nio.ByteBuffer
import scala.collection.immutable.BitSet
import java.util.{BitSet as JBitSet}

package object jdbcutils {

  val printDebugInfo = false

  def bytesToUUID(bytes: Array[Byte]): UUID = {
    require(bytes != null && bytes.length == 16, "UUID must be 16 bytes.")
    val bb   = ByteBuffer.wrap(bytes)
    val high = bb.getLong()
    val low  = bb.getLong()
    new UUID(high, low)
  }

  def convertBitSet(bs: BitSet): JBitSet = {
    val jbs = new JBitSet()
    bs.foreach(jbs.set)
    jbs
  }
}
