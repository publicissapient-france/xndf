package models

import org.specs2.mutable.{Around, Specification}
import de.flapdoodle.embedmongo.{MongodProcess, MongoDBRuntime, MongodExecutable}
import de.flapdoodle.embedmongo.config.MongodConfig
import de.flapdoodle.embedmongo.distribution.Version
import org.bson.types.ObjectId
import org.specs2.execute.Result
import play.api.test.Helpers._
import play.api.test.FakeApplication
import libs.json.DATE_FORMATTER
import play.api.libs.json.Json._
import models.ExpenseFormat._

class ExpenseReportSpec extends Specification {
  val mongodExe: MongodExecutable = MongoDBRuntime.getDefaultInstance.prepare(new MongodConfig(Version.V2_0, 27017, false))
  val mongod: MongodProcess = mongodExe.start()

  def inMemoryMongoDatabase(source:String,name: String = "default"): Map[String, String] = {
    val dbname: String = "play-test-" + scala.util.Random.nextInt
    println("\nSource: "+source+": dbname = "+dbname+"\n")
    Map(
      ("mongodb." + name + ".db" -> dbname)
    )
  }

  object emptyApp extends Around {
    def around[T <% Result](t: => T) = {
      running(FakeApplication(additionalConfiguration = inMemoryMongoDatabase("emptyApp"))) {
        User(new ObjectId, "Jane Doe", "email1", "id1").save()
        t // execute t inside a http session
      }
    }
  }

  object populatedApp extends Around {
    def around[T <% Result](t: => T) = {
      running(FakeApplication(additionalConfiguration = inMemoryMongoDatabase("populatedApp"))) {
        val user: User = User(new ObjectId("aaaaaaaaaaaaaaaaaaaaaaa1"), "Jane Doe", "email1", "id1")
        user.save()
        val date = DATE_FORMATTER.parse("2012-04-17T00:04:00+0200")
        ExpenseReport(new ObjectId("111111111111111111111111"), date, date, user.id, Seq())
          .addLine(date, "xebia", "description", Internet(15.99))
          .addLine(date, "xebia", "description", Internet(15.99)).save
        ExpenseReport(new ObjectId(), date, date, user.id, Seq())
          .addLine(date, "xebia", "description", Internet(15.99))
          .addLine(date, "xebia", "description", Internet(15.99)).save
        t // execute t inside a http session
      }
    }
  }

  "ExpenseReport instance" should {
    "be saved" in emptyApp {
      ExpenseReport.count() === 0
      val date = DATE_FORMATTER.parse("2012-04-17T00:04:00+0200")
      val expenseReport = ExpenseReport(new ObjectId(), date, date, new ObjectId(), Seq())
        .addLine(date, "xebia", "description", Internet(15.99))
        .addLine(date, "xebia", "description", Internet(15.99))
      expenseReport.save
      ExpenseReport.count() === 1
    }
  }

  "ExpenseReport model" should {
    "find all" in populatedApp {
      val all: List[ExpenseReport] = ExpenseReport.findAllByUserId(new ObjectId("aaaaaaaaaaaaaaaaaaaaaaa1"))
      all.length === 2
    }
    "find by id " in populatedApp{
      val Some(expenseReport1)= ExpenseReport.findById(new ObjectId("111111111111111111111111"))
      expenseReport1 must not be None
    }
    "find by userId and id" in populatedApp {
      val date = DATE_FORMATTER.parse("2012-04-17T00:04:00+0200")

      val Some(expenseReport1)= ExpenseReport.findById(new ObjectId("111111111111111111111111"))
      val Some(user) = User.findByVerifiedId("id1")
      expenseReport1.userId===user.id
      println("expenseReport1 in find by userId and id")
      println(expenseReport1.id.toString)
      println(expenseReport1.userId.toString)
      println()

      val Some(expenseReport2)= ExpenseReport.findByIdAndUserID(new ObjectId("111111111111111111111111"),user.id)
      expenseReport2.from === date
      expenseReport2.id ===  new ObjectId("111111111111111111111111")
      expenseReport2.lines.size === 2
      expenseReport1 === expenseReport2

    }
    "convert to and from Json" in {
      val date = DATE_FORMATTER.parse("2012-04-17T00:04:00+0200")

      val userId: ObjectId = new ObjectId()
      val expenseReport = ExpenseReport(new ObjectId(), date, date, userId, Seq())
        .addLine(date, "xebia", "description", Internet(15.99))
        .addLine(date, "xebia", "description", Internet(15.99))

      val jsExpenseReport = toJson(expenseReport)
      val toExpenseReport = jsExpenseReport.as[User => ExpenseReport]
      toExpenseReport(User(userId, "", "", "")) === expenseReport
    }
    "convert to and from Json with NotAssigned" in {
      val date = DATE_FORMATTER.parse("2012-04-17T00:04:00+0200")

      val userId: ObjectId = new ObjectId()
      val expenseReport = ExpenseReport(new ObjectId(), date, date, userId, Seq())
        .addLine(date, "xebia", "description", Internet(15.99))
        .addLine(date, "xebia", "description", Internet(15.99))

      val jsExpenseReport = toJson(expenseReport)
      val toExpenseReport = jsExpenseReport.as[User => ExpenseReport]
      toExpenseReport(User(userId, "", "", "")) === expenseReport
    }
  }
  step(after())

  def after() = {
    mongod.stop()
    mongodExe.cleanup()
  }
}
