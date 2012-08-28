package controllers

import play.api.mvc._
import models.Evidence
import org.bson.types.ObjectId
import play.api.libs.json.{JsValue, JsArray, JsString, JsObject}
import util.parsing.json.JSONObject


object EvidenceController extends Controller with Secured {
  def create = Action(BodyParsers.parse.multipartFormData) { implicit request =>
    val maybeSaved: Seq[(String, Option[ObjectId])] = request.body.files.map(part => part.filename -> Evidence.save(part))

    val result: JsObject = JsObject(
      maybeSaved.map({
        case (filename, Some(id)) => "success" -> JsObject(Seq("filename" -> JsString(filename), "oid" -> JsString(id.toString)))
        case (filename, None) => "error" -> JsString(filename)
      }).groupBy(_._1).map({
        case (key, seq) => key -> JsArray(seq.map(_._2))
      }).toSeq
    )
    Ok(result)
  }
}
