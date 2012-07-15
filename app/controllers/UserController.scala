package controllers

import play.api.libs.json.Json._
import play.api.mvc._
import models.User

object UserController extends Controller with Secured {

  def index = IsAuthenticated { userId => implicit request =>
    val users = User.list();
    Ok(toJson(users))
  }
  
}