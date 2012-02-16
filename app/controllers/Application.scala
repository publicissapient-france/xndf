package controllers

import play.api._
import play.api.libs.json.Json._
import play.api.mvc._
import models.User

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def user(id:Long) = Action { request =>
    val user = User.findById(id)
    user.map( user => Ok(toJson(user.toJson()))).getOrElse(BadRequest("perdu"))
  }
  
}