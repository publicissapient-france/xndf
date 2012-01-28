package play.api.db.evolutions
import play.core._

import play.api._
import play.api.db._
import play.api.libs._
import play.api.libs.Codecs._

class EvolutionsTestPlugin(app: Application) extends Plugin {
  import Evolutions._

  /**
   * Is this plugin enabled.
   *
   * {{{
   * evolutionplugin = disabled
   * }}}
   */
  override lazy val enabled = app.configuration.getConfig("db").isDefined && {
    !app.configuration.getString("evolutionplugin").filter(_ == "disabled").isDefined
  }

  /**
   * Checks the evolutions state.
   */
  override def onStart() {
    val api = app.plugin[DBPlugin].map(_.api).getOrElse(throw new Exception("there should be a database plugin registered at this point but looks like it's not available, so evolution won't work. Please make sure you register a db plugin properly"))

    api.datasources.foreach {
      case (db, (ds, _)) => {
        val script = evolutionScript(api, app.classloader, db)
        script ++ evolutionTestScript(api, app.classloader, "test") 
        if (!script.isEmpty) {
          app.mode match {
            case Mode.Test => Evolutions.applyScript(api, db, script)
            case Mode.Prod if app.configuration.getBoolean("applyEvolutions." + db).filter(_ == true).isDefined => Evolutions.applyScript(api, db, script)
            case Mode.Prod => {
              Logger("play").warn("Your production database [" + db + "] needs evolutions! \n\n" + toHumanReadableScript(script))
              Logger("play").warn("Run with -DapplyEvolutions." + db + "=true if you want to run them automatically (be careful)")

              throw InvalidDatabaseRevision(db, toHumanReadableScript(script))
            }
            case _ => throw InvalidDatabaseRevision(db, toHumanReadableScript(script))
          }
        }
      }
    }
  }
  
  def evolutionTestScript(api: DBApi, applicationClassloader: ClassLoader, db: String) = {
    val application = applicationEvolutions(applicationClassloader, db)

   
    val ups = (application).reverse.map(e => UpScript(e, e.sql_up))
    val downs = (application).map(e => DownScript(e, e.sql_down))

    downs ++ ups
  }

}