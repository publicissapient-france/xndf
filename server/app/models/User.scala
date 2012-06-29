package models

import com.mongodb.casbah.Imports._
import play.api.libs.json._
import org.bson.types.ObjectId
import libs.mongo.DB
import mongoContext._

case class User(id: ObjectId = new ObjectId, name: String, email: String, verifiedId: String) {
  def save() {
    User.withMongo {
      implicit dao =>
        dao.save(this)
    }
  }
}

object User extends DB[User, ObjectId] {
  def withMongo[A] = withDao[A]("users") _

  def list(): List[User] = {
    withMongo {
      implicit dao =>
        dao.find(MongoDBObject.empty).toList
    }
  }

  def findByVerifiedId(verifiedId: String): Option[User] = {
    withMongo {
      implicit dao =>
        dao.findOne(MongoDBObject("verifiedId" -> verifiedId))
    }
}

  /**
   * Authenticate a User.
   */
  def authenticate(name: String, email: String, verifiedId: String): User = {
    withMongo {
      implicit dao =>
        dao.findOne(MongoDBObject("verifiedId" -> verifiedId, "email" -> email)).getOrElse(
          User.create(name, email, verifiedId))
    }
  }

  /**
   * Builds a user from apply and saves it to the database before returning it.
   */
  def create(name: String, email: String, verifiedId: String): User = {
    val user: User = User(new ObjectId, name, email, verifiedId)
    withMongo {
      implicit dao =>
        dao.save(user)
    }
    user
  }

  def count() = {
    withMongo {
      implicit dao =>
        dao.find(MongoDBObject.empty).toList.size
    }
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