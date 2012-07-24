package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.User.XEBIA_MAIL_PATTERN
import models._
import views._
import play.api.libs.openid._
import play.api.libs.concurrent.Thrown
import play.api.libs.concurrent.Redeemed
import play.api.libs.json.JsValue
import play.mvc.Http


trait Application {
  this: Controller with Secured=>

  def index = IsAuthenticated { user=>implicit request =>
    Ok(html.index())
  }

  val loginForm = Form("user" -> nonEmptyText)

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  val REQUIRED_ATTRIBUTES=Seq(
    "email" -> "http://schema.openid.net/contact/email",
    "firstname" -> "http://axschema.org/namePerson/first",
    "lastname" -> "http://axschema.org/namePerson/last"
  )

  def authenticate = Action { implicit request =>
      Logger.debug("in authenticate")
      AsyncResult(
            OpenID.redirectURL(
                "https://www.google.com/accounts/o8/id",
                routes.Application.openIDCallback().absoluteURL(),
                REQUIRED_ATTRIBUTES
                )
          .extend(_.value match {
            case Redeemed(url) =>
              Logger.debug("authenticate redirecting to "+url)
              Redirect(url)

            case Thrown(throwable) =>
              Logger.error("authenticate impossible d'authentifier avec openid",throwable)
              Redirect(routes.Application.login()).flashing("error"->throwable.getMessage)
            }
          )
      )
  }

  def openIDCallback = Action { implicit request =>
    AsyncResult(
      OpenID.verifiedId.extend( _.value match {
        case Redeemed(info) => doAuthenticate(info).toRight[String]("Login non authorisé")
        case Thrown(throwable) => Left(throwable.getMessage)
      }).map( _ match {
        case Right(user) => {
          val uri = request.session.get("before_auth_requested_url").getOrElse(routes.Application.index())
          Redirect(uri.toString,Http.Status.MOVED_PERMANENTLY).withSession("verifiedId"->user.verifiedId)
        }
        case Left(error) => Redirect(routes.Application.login()).flashing("error"->error)
      }
      )
    )
  }

  private def doAuthenticate(info:UserInfo):Option[User] = {
     for {
       email <- info.attributes.get("email").flatMap(XEBIA_MAIL_PATTERN.findFirstIn(_))
       lastname <- info.attributes.get("lastname")
       firstname <- info.attributes.get("firstname")
     } yield User.authenticate(firstname +" "+ lastname,email, info.id)
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.index()).withNewSession.flashing(
      "success" -> "You've been logged out")
  }

}

/**
 * Provide security features
 */
trait Secured {

  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("verifiedId")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = {
    Logger.info("Unauthorized access to "+request.uri+" , redirecting to "+routes.Application.login())
    Results.Redirect(routes.Application.login()).withSession("before_auth_requested_url"-> request.uri)
  }

  // ---

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result): Action[(Action[AnyContent], AnyContent)] = Security.Authenticated(username, onUnauthorized) { userId =>
    Action({  request =>
      Logger.info("Authorized access to "+request.uri+" , for user "+userId)
      f(userId)(request)
    })
  }

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated[A](parser: BodyParser[A])(f: => String => Request[A] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(parser)(request => f(user)(request))
  }
}

object Application extends Controller with Secured with Application
