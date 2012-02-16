package models

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import play.api.db.DB
import anorm._
import anorm.SqlParser._
import play.api.Logger

@RunWith(classOf[JUnitRunner])
class UserSpec extends Specification {

  import models.User

  "User model" should {
    "must save a user" in {

      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        user1.save() === 1001 and
        User.count() === 2
      }
    }
    "be retrieved by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val Some(user)=User.findById(1000)
        user.firstName must equalTo("nadia")
      }
    }
    "be converted from and to json" in {
      User.fromJson(user2.toJson()) === Right(user2)
    }
  }
  private val user1 = User(NotAssigned, "firstname", "lastname", "email")
  private val user2 = User(Id(1), "firstname", "lastname", "email")
}