import de.flapdoodle.embedmongo.config.MongodConfig
import de.flapdoodle.embedmongo.distribution.Version
import de.flapdoodle.embedmongo.{MongodProcess, MongodExecutable, MongoDBRuntime}
import play.api.{Application, GlobalSettings}

import play.api.Mode
/**
 * @author David Galichet.
 */

object Global extends GlobalSettings {

  override def beforeStart(app: Application) {
    app.mode match {
      case Mode.Dev => startMongo(app)
      case _ => Unit
    }

    super.beforeStart(app)
  }

  lazy val _mongodExe:MongodExecutable ={
    val runtime:MongoDBRuntime = MongoDBRuntime.getDefaultInstance();
    runtime.prepare(new MongodConfig(Version.V2_0, 27017,false));
  }
  lazy val _mongod:MongodProcess={_mongodExe.start()}

  def startMongo(app:Application) {
    _mongod
  }
  override def onStop(app: Application) {
    _mongod.stop()
    _mongodExe.cleanup()
  }
}
