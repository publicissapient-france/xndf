package controllers

import play.api.mvc._


object EvidenceController extends Controller with Secured {
  def create = Action(BodyParsers.parse.multipartFormData) { implicit request =>
//    request.body.files.map( file =>
//      Evidence.save(file)
//    )
    Ok("")
  }
}
