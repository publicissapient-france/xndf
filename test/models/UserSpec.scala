package models

import org.specs2.mutable.{Around, Specification}
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json._
import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import de.flapdoodle.embedmongo.{MongodProcess, MongoDBRuntime, MongodExecutable}
import de.flapdoodle.embedmongo.config.MongodConfig
import de.flapdoodle.embedmongo.distribution.Version
import models.User.UserFormat.reads
import org.specs2.execute.Result

class UserSpec extends Specification {

  val mongodExe: MongodExecutable = MongoDBRuntime.getDefaultInstance().prepare(new MongodConfig(Version.V2_0, 27017, false))
  val mongod: MongodProcess = mongodExe.start();

  def inMemoryMongoDatabase(name: String = "default"): Map[String, String] = {
    val dbname: String = "play-test-" + scala.util.Random.nextInt
    println(dbname)
    Map(
      ("mongodb." + name + ".db" -> dbname)
    )
  }

  object emptyApp extends Around {
    def around[T <% Result](t: => T) = {
      running(FakeApplication(additionalConfiguration = inMemoryMongoDatabase())) {
        t // execute t inside a http session
      }
    }
  }

  object populatedApp extends Around {
    def around[T <% Result](t: => T) = {
      running(FakeApplication(additionalConfiguration = inMemoryMongoDatabase())) {
        User(new ObjectId, "Jane Doe", "email1", "id1").save()
        User(new ObjectId, "John Smith", "email2", "id2").save()
        t // execute t inside a http session
      }
    }
  }

  "User instance" can {
    "be saved to mongo" in emptyApp {
      User.count() === 0
      val user: User = User(new ObjectId, "Jane Doe", "email2", "id2")
      user.save()
      User.count() === 1
    }
  }

  "A user " can {
    "be retrieved by verifiedId" in populatedApp {
      val Some(user) = User.findByVerifiedId("id2")
      user.name === "John Smith"
    }

    "be converted from and to json" in {
      val user: User = User(new ObjectId, "Jane Doe", "email2", "id2")

      val json: JsValue = toJson(user)
      val json1: User = reads(json)
      json1 === user
    }

    "be created and saved in the database" in populatedApp {
      val u = User.create("Josiane", "email3", "id3")
      User.count() === 3 and u.name === User.findByVerifiedId("id3").get.name
    }

    "be authenticated when user exists" in populatedApp {
      val u = User.create("Josiane", "email3", "id3")
      val authenticated = User.authenticate("Josiane", "email3", "id3")
      User.count() === 3 and u === authenticated
    }
    "be authenticated when user does not exist" in populatedApp {
      val authenticated = User.authenticate("Josiane", "email3", "id3")
      User.count() === 3
    }
  }
  step(after())

  def after() = {
    mongod.stop()
    mongodExe.cleanup()
  }
}