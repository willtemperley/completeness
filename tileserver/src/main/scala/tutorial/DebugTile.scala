package tutorial

import java.io.{File, FileOutputStream}
import java.nio.ByteBuffer

import org.apache.hadoop.hbase.client.Scan
import xyz.tms.TmsTile

/**
  * Created by willtemperley@gmail.com on 21-Nov-16.
  */
object DebugTile {

  def main(args: Array[String]): Unit = {
    val tab = AccessHbase.getTable("buffer14")

    val scan = new Scan()
    scan.addFamily(TmsTile.cf)
    val res = tab.getScanner(scan)

    val iterator = res.iterator()
    while (iterator.hasNext) {

      val res = iterator.next()
      val v = res.getValue(TmsTile.cf, TmsTile.cimg)

      val t = new TmsTile(res.getRow)
      println(t)

      writeDebugTile(t, v)
    }

  }
  private def writeDebugTile(tile: TmsTile, bytes: Array[Byte]) {
    val f: File = new File("/tmp/ras/mr-" + tile.toString + ".png")
    val fileOutputStream: FileOutputStream = new FileOutputStream(f)
    for (aByte <- bytes) {
      fileOutputStream.write(aByte)
    }
  }

}
