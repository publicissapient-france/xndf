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


object Application extends Controller with Secured {

  def index = IsAuthenticated { user=>implicit request =>
    Ok(html.index())
  }

  val loginForm = Form("user" -> nonEmptyText)

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def authenticate = Action { implicit request =>
      AsyncResult(
            OpenID.redirectURL(
                "https://www.google.com/accounts/o8/id",
                routes.Application.openIDCallback().absoluteURL(),
                Seq(
                  "email" -> "http://schema.openid.net/contact/email",
                  "firstname" -> "http://axschema.org/namePerson/first",
                  "lastname" -> "http://axschema.org/namePerson/last"
                    )
                )
          .extend(_.value match {
            case Redeemed(url) => {
              Logger.debug("redirecting to "+url)
              Redirect(url)
            }
            case Thrown(t) => {
              Logger.error("impossible d'authentifier avec openid",t)
              Redirect(routes.Application.login())
            }
          }))
  }

  def openIDCallback = Action { implicit request =>    
	  AsyncResult(
	    OpenID.verifiedId.extend( _.value match {
	      case Redeemed(info) => doAuthenticate(info,request)
	      case Thrown(t) => {
	        // Here you should look at the error, and give feedback to the user
          Logger.error("impossible d'authentifier avec openid",t)
          Redirect(routes.Application.login())
	      }
	    })
  )
  }
  
  private def doAuthenticate(info:UserInfo, request:Request[AnyContent]) = {
    val user: Option[User] = for{
      email <- info.attributes.get("email").flatMap(XEBIA_MAIL_PATTERN.findFirstIn(_))
      lastname <- info.attributes.get("lastname")
      firstname <- info.attributes.get("firstname")
    } yield User.authenticate(firstname +" "+ lastname,email, info.id)
    user.map{ u =>
      val uri = request.session.get("before_auth_requested_url").getOrElse(routes.Application.index())
      Redirect(uri.toString,301).withSession("verifiedId"->u.verifiedId) }
      .getOrElse(Redirect(routes.Application.login()))
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
    Results.Redirect(routes.Application.login()).withSession("before_auth_requested_url"-> request.uri);
  }

  // ---

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { userId =>
    Action(request => f(userId)(request))
  }

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated[A](parser: BodyParser[A])(f: => String => Request[A] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(parser)(request => f(user)(request))
  }
}
