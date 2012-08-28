package libs

import play.api.{Play, Application, PlayException}
import play.api.Play.current
import se.radley.plugin.salat.SalatPlugin
import com.mongodb.casbah.gridfs.GridFS
import com.mongodb.casbah.{MongoDB, MongoConnection}

package object mongo {
  def gridFS(collectionName: String, sourceName:String = "default")(implicit app: Application): GridFS = {
    app.plugin[SalatPlugin].map({ plugin =>
      val connection: MongoConnection = plugin.source(sourceName).connection
      val db: MongoDB = connection(plugin.source(sourceName).db)
      GridFS(db)
    }).getOrElse(throw PlayException("SalatPlugin is not registered.", "You need to register the plugin with \"500:se.radley.plugin.salat.SalatPlugin\" in conf/play.plugins"))
  }
}