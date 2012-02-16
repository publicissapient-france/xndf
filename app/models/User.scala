package models
import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

case class User(id: Pk[Long], firstName: String, lastName: String, email: String) {
  def save() = {
    DB.withConnection { implicit connection =>
      val id = SQL("select next value for users_seq").as(scalar[Long].single)
      SQL("insert into users (id, firstname, lastname, email) values ({id}, {firstname}, {lastname}, {email})")
        .on('id -> id, 'firstname -> firstName, 'lastname -> lastName, 'email -> email).executeUpdate()
    }
    id
  }
}

object User {

  /**
   * Parse a Project from a ResultSet
   */
  val simple = {
	  get[Pk[Long]]("users.id") ~
	  get[String]("users.firstname") ~
	  get[String]("users.lastname") ~
	  get[String]("users.email") map {
	    case id ~ firstName ~ lastName ~ email => User(id, firstName, lastName, email)
	  }
  }

  def findById(id: Long): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where id = {id}")
        .on('id -> id).as(User.simple.singleOpt)
    }
  }

  def count() = {
    DB.withConnection { implicit connection =>
      SQL("select count(1) from users").as(scalar[Long].single)
    }
  }
}