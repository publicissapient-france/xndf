package models

import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import play.api.Play.current
import play.api.libs.json._
import org.bson.types.ObjectId

case class User(id: ObjectId = new ObjectId, name: String, email: String, verifiedId: String) {
  def save() = {
    User.withDao(implicit dao =>
      dao.save(this)
    )
  }
}

object User {

  def withDao[A, T](block: SalatDAO[User, ObjectId] => A): A = {
    val dao = new SalatDAO[User, ObjectId](collection = mongoCollection("user")) {}
    block(dao)
  }

  def list(): List[User] = {
    withDao(implicit dao =>
      dao.find(MongoDBObject.empty).toList
    )
  }

  def findByVerifiedId(verifiedId: String): Option[User] = {
    withDao(implicit dao =>
      dao.findOne(MongoDBObject("verifiedId" -> verifiedId))
    )
  }

  /**
   * Authenticate a User.
   */
  def authenticate(name: String, email: String, verifiedId: String): User = {
    withDao(implicit dao =>
      dao.findOne(MongoDBObject("verifiedId" -> verifiedId, "email" -> email)).getOrElse(
        User.create(name, email, verifiedId)
      )
    )
  }

  /**
   * Builds a user from apply and saves it to the database before returning it.
   */
  def create(name: String, email: String, verifiedId: String): User = {
    val user: User = User(new ObjectId, name, email, verifiedId)
    withDao(implicit dao =>
      dao.save(user)
    )
    user
  }

  def count() = {
    withDao(implicit dao =>
      dao.find(MongoDBObject.empty).toList.size
    )
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