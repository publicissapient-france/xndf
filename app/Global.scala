import models.User
import anorm._
import play.api.{Application, GlobalSettings}

/**
 * @author David Galichet.
 */

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    if (User.count() == 0) {
      User(NotAssigned, "nadia","play","nadia@example.com").save()
    }
  }
}
