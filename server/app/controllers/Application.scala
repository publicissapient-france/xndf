package controllers

import play.api._
import play.api.libs.json.Json._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import views._
import play.api.libs.openid._
import play.api.libs.concurrent.Thrown
import play.api.libs.concurrent.Redeemed


object Application extends Controller with Secured {

  def index = IsAuthenticated { user => implicit request =>
     Ok("")
    //Ok(html.index(user))
  }

  val loginForm = Form(
    "user" -> text)

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    Form(single(
      "openid" -> nonEmptyText)).bindFromRequest.fold(
      error => {
        Logger.info("bad request " + error.toString)
        BadRequest(error.toString)
      },
      {
        case (openid) => AsyncResult(
            OpenID.redirectURL(
                openid, 
                routes.Application.openIDCallback().absoluteURL(),
                Seq("email" -> "http://schema.openid.net/contact/email")
                )
          .extend(_.value match {
            case Redeemed(url) => Redirect(url)
            case Thrown(t) => Redirect(routes.Application.login())
          }))
      })
  }

  def openIDCallback = Action { implicit request =>    
	  AsyncResult(
	    OpenID.verifiedId.extend( _.value match {
	      case Redeemed(info) => doAuthenticate(info)
	      case Thrown(t) => {
	        // Here you should look at the error, and give feedback to the user
	        Redirect(routes.Application.login)
	      }
	    })
  )
  }
  
  private def doAuthenticate(info:UserInfo) = {
    val user = for{ 
      email <- info.attributes.get("email")      
    } yield User.authenticate("name",email, info.id)
    user.map{ u => 
      			Ok(u.id + "\n" + info.attributes).withSession("verifiedId"->u.verifiedId) }
    	.getOrElse(Redirect(routes.Application.login))
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
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
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)

  // ---

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  /**
   * Check if the connected user is a owner of this task.
   *
  
  def IsOwnerOf(task: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user =>
    request =>
      if (Task.isOwner(task, user)) {
        f(user)(request)
      } else {
        Results.Forbidden
      }
  }*/

}
