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
    "be retrieved by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
	    import play.api.Play.current
        DB.withConnection { implicit connection =>
	      SQL("insert into users(id,firstName, lastName, email) values(1,'nadia','play','nadia@example.com')").executeUpdate()
	    }
        val Some(user)=User.findById(1)        
        user.firstName must equalTo("nadia")
      }
    }
  }
}