package models

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import anorm._
import play.api.libs.json.Json._

class UserSpec extends Specification {

  import models.User
  import models.User._


  "User model" should {
    "save a user" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        user1.save() === 1000 and
        User.count() === 1
      }
    }

    "be retrieved by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        user1.save()
        user2.save()
        val Some(user)=User.findById(1000)
        user.name must equalTo(user1.name)
      }
    }

    "be retrieved by verifiedId" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        user1.save()
        user2.save()
        val Some(user)=User.findByVerifiedId("id2")
        user.name must equalTo(user2.name)
      }
    }

    "be converted from and to json" in {
      fromJson(toJson(user2)) === user2
    }

    "be created and saved in the database" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val u = User.create("Josiane", "email3", "id3")
        User.count() === 1 and u.name === User.findByVerifiedId("id3").get.name
      }
    }

    "be authenticated when user exists" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val u = User.create("Josiane", "email3", "id3")
        val authenticated=User.authenticate("Josiane", "email3", "id3")
        User.count() === 1 and u === authenticated
      }
    }
    "be authenticated when user does not exist" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val authenticated=User.authenticate("Josiane", "email3", "id3")
        User.count() === 1 and authenticated.id === Id(1000)
      }
    }
  }

  private val user1 = User(NotAssigned, "John Doe", "email1", "id1")
  private val user2 = User(Id(1), "Jane Doe", "email2", "id2")
}