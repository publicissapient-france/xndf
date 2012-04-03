package controllers

import play.api._
import play.api.libs.json.Json._
import play.api.mvc._
import models.User
import models.User._

object UserController extends Controller with Secured {

  def index = IsAuthenticated { userId => implicit request =>
    val users = User.findAll();
    Ok(toJson(users))
  }
  def show(id:Long) = Action { request =>
    val user = User.findById(id)
    user.map( user => Ok(toJson(user))).getOrElse(BadRequest("perdu"))
  }
  
}