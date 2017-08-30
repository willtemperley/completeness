package tutorial

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client._
import java.io.IOException
import java.io.InputStream
import java.util.Properties


/**
  * Created by willtemperley@gmail.com on 16-Nov-16.
  */
object AccessHbase {


  /**
    * Loads and wraps an hbase connection
    *
    * Created by willtemperley@gmail.com on 14-Jul-16.
    */

  def getTable(tableName: String): Table = connection.getTable(TableName.valueOf(tableName))

  val connection = ConnectionFactory.createConnection(getConfiguration)

  def getConfiguration: Configuration = {

    val configuration = new Configuration()
    val props = new Properties()
    val loader = Thread.currentThread().getContextClassLoader
    val resourceStream = loader.getResourceAsStream("hbase-config.properties")

    props.load(resourceStream)

    val quorum = "hbase.zookeeper.quorum"
    val master = "hbase.master"
    configuration.set(quorum, props.getProperty(quorum))
    configuration.set(master, props.getProperty(master))

    configuration
  }

}

