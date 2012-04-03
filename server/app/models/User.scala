package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current
import play.api.libs.json._

case class User(id: Pk[Long], name: String, email: String, verifiedId: String) {
  def save() = {
    DB.withConnection { implicit connection =>
      val newId = SQL("select next value for users_seq").as(scalar[Long].single)
      SQL("insert into users (id, name, email, verifiedId) values ({id}, {name}, {email}, {verifiedId})")
        .on('id -> newId, 'name -> name, 'email -> email, 'verifiedId->verifiedId).executeUpdate()
      newId
    }
  }

  def toJson() = JsObject(Seq("id" -> JsNumber(id.get), "name" -> JsString(name),
    "email" -> JsString(email)))
}

object User {

  /**
   * Parse a Project from a ResultSet
   */
  val simple = {
	  get[Pk[Long]]("users.id") ~
	  get[String]("users.name") ~	
	  get[String]("users.email") ~ 
	  get[String]("users.verifiedId") map {
	    case id ~ name ~ email ~ verifiedId => User(id, name, email, verifiedId)
	  }
  }

  def findById(id: Long): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where id = {id}")
        .on('id -> id).as(User.simple.singleOpt)
    }
  }
  
  def findByVerifiedId(verifiedId: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where verifiedId = {id}")
        .on('id -> verifiedId).as(User.simple.singleOpt)
    }
  }
  /**
   * Authenticate a User.
   */
  def authenticate(name: String, email: String, verifiedId: String): User = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from users where
         email = {email} and verifiedId = {verifiedId}
        """
      ).on(
        'email -> email,
        'verifiedId -> verifiedId
      ).as(User.simple.singleOpt).getOrElse(
        User.create(name, email,verifiedId)
      )

    }
  }

  /**
   * Builds a user from apply and saves it to the database before returning it.
   */
  def create(name:String,  email:String, verifiedId:String) = {
    val u =User(NotAssigned, name, email,verifiedId)
    User(Id(u.save()), name, email, verifiedId)
  }

  def count() = {
    DB.withConnection { implicit connection =>
      SQL("select count(1) from users").as(scalar[Long].single)
    }
  }

  def fromJson(json:JsValue, verifiedId: String):Either[String, User] = {
    try {
      Right(User(
        Id((json \ "id").as[Long]),
        (json \ "name").as[String],
        (json \ "email").as[String],
        verifiedId)
      )
    } catch {
      case e:Exception => Left("Perdu" + e)
    }
  }
}