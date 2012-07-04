package models

import java.util.Date
import com.mongodb.casbah.Imports._
import org.bson.types.ObjectId
import libs.mongo.DB

import play.api.libs.json._
import play.api.libs.json.Json._
import libs.json._
import com.novus.salat.annotations.raw.Salat

case class ExpenseReport(id: ObjectId, from: Date, to: Date, userId: ObjectId, _lines: Seq[ExpenseLine]) {
  lazy val lines = _lines

  def addLine(valueDate: Date, account: String, description: String, expense: Expense) = {
    lazy val newParent: ExpenseReport = ExpenseReport(this.id, this.from, this.to, this.userId, line +: this.lines)
    lazy val line: ExpenseLine = ExpenseLine(valueDate, account, description, expense)
    newParent
  }

  def total = {
    lines.map(l => l.expense.amount).sum
  }

  def save() {
    ExpenseReport.withMongo {
      implicit dao =>
        dao.save(this)
    }
  }
}

case class ExpenseLine(valueDate: Date,
                       account: String,
                       description: String,
                       expense: Expense) {
}

object ExpenseLine {

  implicit object ExpenseLineFormat extends Format[ExpenseLine] {
    def reads(value: JsValue): ExpenseLine = {
      ExpenseLine(
        (value \ "valueDate").as[Date],
        (value \ "account").as[String],
        (value \ "description").as[String],
        ((value \ "expenseType").as[String], (value \ "expense").as[Double])
      )
    }

    def writes(line: ExpenseLine) = {
      JsObject(
        Seq(
          "valueDate" -> toJson(line.valueDate),
          "account" -> JsString(line.account),
          "description" -> JsString(line.description),
          "expense" -> JsNumber(line.expense.amount),
          "expenseType" -> JsString(line.expense.qualifier)
        )
      )
    }
  }

}

object ExpenseReport extends DB[ExpenseReport, ObjectId] {

  def withMongo[A] = withDao[A]("expenses") _

  def findAllByUserId(userId: ObjectId): List[ExpenseReport] = {
    withMongo {
      implicit dao =>
        dao.find(MongoDBObject("userId"->userId)).toList
    }
  }

  def findByIdAndUserID(id: ObjectId, userId: ObjectId): Option[ExpenseReport] = {
    withMongo {
      implicit dao =>
        dao.findOne(MongoDBObject("userId" -> userId,"_id" -> id))
    }
  }

  def findById(id: ObjectId): Option[ExpenseReport] = {
    withMongo {
      implicit dao =>
        dao.findOneById(id)
    }
  }

  def count() = {
    withMongo {
      implicit dao =>
        dao.count(MongoDBObject.empty)
    }
  }

  implicit object ExpenseReportReads extends Reads[User => ExpenseReport] {
    def reads(json: JsValue) = {
      user =>
        val expenseReportId: ObjectId = (json \ "id").asOpt[String].map(new ObjectId(_)).getOrElse(new ObjectId())
        ExpenseReport(
          expenseReportId,
          (json \ "startDate").as[Date],
          (json \ "endDate").as[Date],
          user.id,
          (json \ "lines").as[Seq[ExpenseLine]]
        )

    }
  }

  implicit object ExpenseReportWrites extends Writes[ExpenseReport] {
    def writes(report: ExpenseReport) = {
      toJson(
        Map(
          "id" -> toJson(report.id.toString),
          "userId" -> toJson(report.userId.toString),
          "startDate" -> toJson(report.from),
          "endDate" -> toJson(report.to),
          "total" -> toJson(report.total),
          "lines" -> toJson(report.lines)
        )
      )
    }
  }

}


object Expense {

  implicit def tupleToExpense(tuple: (String, Double)): Expense = {
    val (qualifier, amount) = tuple
    qualifier match {
      case "Lodging" => Lodging(amount)
      case "Transportation" => Transportation(amount)
      case "Gas" => Gas(amount)
      case "Meal" => Meal(amount)
      case "Phone" => Phone(amount)
      case "Internet" => Internet(amount)
      case "Other" => Other(amount)
    }

  }
}

@Salat
sealed trait Expense {
  val amount: Double
  val qualifier: String
}

case class Lodging(amount: Double) extends Expense {
  val qualifier = "Lodging"
}

case class Transportation(amount: Double) extends Expense {
  val qualifier = "Transportation"
}

case class Gas(amount: Double) extends Expense {
  val qualifier = "Gas"
}

case class Meal(amount: Double) extends Expense {
  val qualifier = "Meal"
}

case class Phone(amount: Double) extends Expense {
  val qualifier = "Phone"
}

case class Internet(amount: Double) extends Expense {
  val qualifier = "Internet"
}

case class Other(amount: Double) extends Expense {
  val qualifier = "Other"
}

