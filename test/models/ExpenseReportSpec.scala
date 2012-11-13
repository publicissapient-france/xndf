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
import org.specs2.specification.{Step, Fragments}


trait Served extends org.specs2.mutable.Specification {

  val port = 27180
  lazy val mongodExe:MongodExecutable=  MongoDBRuntime.getDefaultInstance.prepare(new MongodConfig(Version.V2_0, 27181, false))
  lazy val mongod:MongodProcess = mongodExe.start()

  lazy val startMongo={
    println("Starting mongo")
    mongod.hashCode()
  } //BeforeClass

  lazy val stopMongo={
    println("Killing mongo")
    mongod.stop()
    mongodExe.cleanup()
  } //AfterClass
}
class MongoDependantSpec extends Served {
  override val port = 27181
  step(startMongo)
  "whatever" in success
  step(stopMongo)
}
class ExpenseReportSpec extends Served {
  override val port = 27181

  def inMemoryMongoDatabase(source:String,name: String = "default"): Map[String, String] = {
    val dbname: String = "play-test-" + scala.util.Random.nextInt
    println("Source: "+source+": dbname = "+dbname+"")
    Map(
      ("mongodb."+name+".uri" -> ("mongodb://127.0.0.1:"+port+"/"+dbname))
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
        ExpenseReport(new ObjectId("111111111111111111111111"), date, date, user.id, Seq(),Some(ExpenseStatus.SUBMITTED))
          .addLine(date, "xebia", "description", Internet(15.99),Seq())
          .addLine(date, "xebia", "description", Internet(15.99),Seq()).save()
        ExpenseReport(new ObjectId(), date, date, user.id, Seq(),Some(ExpenseStatus.SUBMITTED))
          .addLine(date, "xebia", "description", Internet(15.99),Seq())
          .addLine(date, "xebia", "description", Internet(15.99),Seq()).save()
        t // execute t inside a http session
      }
    }
  }

  step(startMongo)

  "ExpenseReport instance" should {
    "be saved" in emptyApp {
      ExpenseReport.count() === 0
      val date = DATE_FORMATTER.parse("2012-04-17T00:04:00+0200")
      val expenseReport = ExpenseReport(new ObjectId(), date, date, new ObjectId(), Seq(),Some(ExpenseStatus.SUBMITTED))
        .addLine(date, "xebia", "description", Internet(15.99),Seq())
        .addLine(date, "xebia", "description", Internet(15.99),Seq())
      expenseReport.save()
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
      val Some(user) = User.findByEmail("email1")
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
      val expenseReport = ExpenseReport(new ObjectId(), date, date, userId, Seq(),Some(ExpenseStatus.DRAFT))
        .addLine(date, "xebia", "description", Internet(15.99),Seq())
        .addLine(date, "xebia", "description", Internet(15.99),Seq())

      val jsExpenseReport = toJson(expenseReport)
      val toExpenseReport = jsExpenseReport.as[User => ExpenseReport]
      toExpenseReport(User(userId, "", "", "")) === expenseReport
    }
    "convert to and from Json with NotAssigned" in {
      val date = DATE_FORMATTER.parse("2012-04-17T00:04:00+0200")

      val userId: ObjectId = new ObjectId()
      val expenseReport = ExpenseReport(new ObjectId(), date, date, userId, Seq(),Some(ExpenseStatus.DRAFT))
        .addLine(date, "xebia", "description", Internet(15.99),Seq())
        .addLine(date, "xebia", "description", Internet(15.99),Seq())

      val jsExpenseReport = toJson(expenseReport)
      val toExpenseReport = jsExpenseReport.as[User => ExpenseReport]
      toExpenseReport(User(userId, "", "", "")) === expenseReport
    }
    "compute subtotal for each expense type" in {
      val date = DATE_FORMATTER.parse("2012-04-17T00:04:00+0200")

      val userId: ObjectId = new ObjectId()
      val expenseReport = ExpenseReport(new ObjectId(), date, date, userId, Seq(),Some(ExpenseStatus.DRAFT))
        .addLine(date, "xebia", "description", Internet(15.00),Seq())
        .addLine(date, "xebia", "description", Internet(15.00),Seq())
        .addLine(date, "xebia", "description", Phone(20.00),Seq())
        .addLine(date, "xebia", "description", Phone(20.00),Seq())
      val  expected = Seq(Internet(30.00), Phone(40.00))
      expenseReport.subtotals == expected
    }
  }

  step(stopMongo)

}
