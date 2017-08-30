package tutorial

import akka.actor._
import akka.io.IO
import java.io._
import java.nio.ByteBuffer
import java.nio.file.{Files, Paths}
import java.util
import javax.xml.bind.DatatypeConverter

import scala.collection.JavaConversions._
import com.esri.core.geometry._
import org.apache.commons.io.output
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos.TableName
import org.apache.hadoop.hbase.client.{Get, Table}
import org.apache.hadoop.hbase.util.Bytes
import org.openstreetmap.osmosis.core.util.TileCalculator
import spray.can.Http
import spray.http.MediaTypes
import spray.routing.{HttpService, Route}
import xyz.TextToGraphics
import xyz.tms.{TmsTile, TmsTileCalculator}

import scala.concurrent._

object ServeTile {
  //  val catalogPath = new java.io.File("data/catalog").getAbsolutePath

  // Create a reader that will read in the indexed tiles we produced in IngestImage.

  def main(args: Array[String]): Unit = {
    implicit val system = akka.actor.ActorSystem("tile-system")

    // create and start our service actor
    val service = system.actorOf(Props(classOf[TileServiceActor]), "tile")

    // start a new HTTP server on port 8080 with our service actor as the handler
    IO(Http) ! Http.Bind(service, "0.0.0.0", 8085)
  }
}

class TileServiceActor extends Actor with HttpService {

  val tileCF = Bytes.toBytes("d")
  val tileCol = Bytes.toBytes("i")

  import scala.concurrent.ExecutionContext.Implicits.global

  def actorRefFactory = context

  def receive = runRoute(staticRoute ~ debugGeomRoute ~ myService)

  def staticRoute = pathPrefix("static") {
    getFromResourceDirectory("static")
  }

  def myService =
    pathPrefix("ras" / IntNumber / IntNumber / IntNumber) { (zoom, x, y) =>
      respondWithMediaType(MediaTypes.`image/png`) {
        complete {
          future {

//            val tile = new TmsTile(x, y, zoom)
            getRaster(x, y, zoom)

          }
        }
      }
    }

  def getWater: Array[Byte] = {

    val file = "src/main/resources/static/images/water.png"
    val b = Files.readAllBytes(Paths.get(file))
    b
  }

  def getDebugTile(x: Int, y: Int, z: Int): Array[Byte] = {

    val txt = z + "," + x + "," + y

    TextToGraphics.renderText(txt)

  }

  def getRaster(x: Int, y: Int, z: Int): Array[Byte] = {

    val table = AccessHbase.getTable("buffer14")

    val tile = new TmsTile(x, y, z)
    val get = new Get(tile.encode())
    //    val result = table.get(new Get(TileCalculator.encodeTile(tile)))
    val result = table.get(get)

    val img = result.getValue(tileCF, tileCol)

    if (img == null)  {
      println("null tile: " + tile)
      val exists = table.exists(get)
      println("row exists: " + exists)
      return getDebugTile(x, y, z)
    }

    img
  }

  def writeToFile(file: String, data: Seq[String]): Unit = {
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))
    for (x <- data) {
      writer.write(x + "\n") // however you want to format it
    }
    writer.close()
  }


  def debugGeomRoute = pathPrefix("geom" / IntNumber) { (zoom) => {

    respondWithMediaType(MediaTypes.`application/javascript`) {

      complete {
        val x: MapGeometry = getDebugGeom("brazil")

        //        val wkt = OperatorExportToWkt.local()
        //        val lines = polygons.map(f => wkt.execute(WktExportFlags.wktExportPolygon, f, null))
        //
        //        writeToFile("e:/tmp/ras/lines.txt", lines)

        val env = new Envelope2D()
        x.getGeometry.queryEnvelope2D(env)
        val tiles = TmsTileCalculator.tilesForEnvelope(env, zoom)

        println(zoom)
//        val scale = tiles.get(0).getScale
//        println(scale)

        val polygons = tiles.map(_.getEnvelopeAsPolygon)

        val geojson = OperatorExportToGeoJson.local()
        val geojsons = polygons.map(f => geojson.execute(0, x.getSpatialReference, f))
        val output = geojsons.mkString(",")
        val mada = "var madagascar = [" + output + "]"

        mada
      }
    }
  }
  }

  def getDebugGeom(resourceName: String): MapGeometry = {
    val file = new File("src/main/resources/" + resourceName + ".geo.json")
    val local = OperatorImportFromGeoJson.local()
    val json = scala.io.Source.fromFile(file).getLines.mkString
    val x: MapGeometry = local.execute(0, Geometry.Type.Polygon, json, null)
    x
  }

//  def root =
//    pathPrefix(IntNumber / IntNumber / IntNumber) { (zoom, x, y) =>
//      respondWithMediaType(MediaTypes.`image/png`) {
//        complete {
//          future {
//
//            val xD = (360D / (2 << zoom)) * x - 180
//            val yD = (180D / (1 << zoom)) * y - 90
//
//            //            val txt = zoom + "," + xD + "," + yD
//            val txt = zoom + "," + x + "," + y
//            //            println(txt)
//
//            TextToGraphics.renderText(txt)
//
//          }
//        }
//      }
//    }
}
