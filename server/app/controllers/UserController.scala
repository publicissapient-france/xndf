package controllers

import play.api._
import play.api.libs.json.Json._
import play.api.mvc._
import models.User

object UserController extends Controller {
  
  def user(id:Long) = Action { request =>
    val user = User.findById(id)
    user.map( user => Ok(toJson(user.toJson()))).getOrElse(BadRequest("perdu"))
  }
  
}