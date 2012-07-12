package models

import com.mongodb.casbah.Imports._
import play.api.libs.json._
import org.bson.types.ObjectId
import com.novus.salat.dao.{ModelCompanion, SalatDAO}
import se.radley.plugin.salat._
import play.api.libs.json.JsString
import play.api.libs.json.JsObject
import play.api.Play.current
import models.mongoContext._

case class User(id: ObjectId = new ObjectId, name: String, email: String, verifiedId: String) {
  def save() {
    User.save(this)
  }
}

object User extends ModelCompanion[User, ObjectId] {
  val XEBIA_MAIL_PATTERN = "(.*@xebia.fr)".r

  def dao = {
    new SalatDAO[User, ObjectId](collection = mongoCollection("users")) {}
  }

  def list(): List[User] = {
    find(MongoDBObject.empty).toList
  }

  def findByVerifiedId(verifiedId: String): Option[User] = {
    findOne(MongoDBObject("verifiedId" -> verifiedId))
  }

  /**
   * Authenticate a User.
   */
  def authenticate(name: String, email: String, verifiedId: String): User = {
    findOne(MongoDBObject("verifiedId" -> verifiedId, "email" -> email)).getOrElse(
      User.create(name, email, verifiedId))
  }

  /**
   * Builds a user from apply and saves it to the database before returning it.
   */
  def create(name: String, email: String, verifiedId: String): User = {
    val user: User = User(new ObjectId, name, email, verifiedId)
    dao.save(user)
    user
  }

  def count() = {
    find(MongoDBObject.empty).toList.size
  }

  implicit object UserFormat extends Format[User] {

    implicit def reads(json: JsValue): User = {
      User(
        new ObjectId((json \ "id").as[String]),
        (json \ "name").as[String],
        (json \ "email").as[String],
        (json \ "verifiedId").as[String])
    }

    //unmarshaling to JSValue is covered in the next paragraph

    implicit def writes(u: User): JsValue = JsObject(
      Seq("id" -> JsString(u.id.toString),
        "name" -> JsString(u.name),
        "email" -> JsString(u.email),
        "verifiedId" -> JsString(u.verifiedId)
      )
    )
  }

}