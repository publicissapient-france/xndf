package models

import java.util.Date
import anorm._
import play.api.libs.json._
import play.api.libs.json.Json._
import libs.json._

case class ExpenseReport(id: Pk[Long], from: Date, to: Date, userId: Pk[Long], _lines: Seq[ExpenseLine]) {
  lazy val lines = _lines

  def addLine(id: Pk[Long], valueDate: Date, account: String, description: String, expense: Expense) = {
    lazy val newParent: ExpenseReport = ExpenseReport(this.id, this.from, this.to, this.userId, line +: this.lines)
    lazy val line: ExpenseLine = ExpenseLine(id, this.id, valueDate, account, description, expense)
    newParent
  }
}

case class ExpenseLine(id: Pk[Long],
                       expenseReportId: Pk[Long],
                       valueDate: Date,
                       account: String,
                       description: String,
                       expense: Expense) {
}

object ExpenseLine {
  implicit object ExpenseLineFormat extends Format[ExpenseLine]{
    def reads(value: JsValue): ExpenseLine = {
      ExpenseLine(
        (value \ "id").as[Pk[Long]],
        (value \ "expenseReportId").as[Pk[Long]],
        (value \ "valueDate").as[Date],
        (value \ "account").as[String],
        (value \ "description").as[String],
        ((value \ "expenseType").as[String],(value \ "expense").as[Double])
      )
    }

    def writes(line: ExpenseLine) = {
      toJson(
        Map(
          "id" -> toJson(line.id),
          "expenseReportId" -> toJson(line.expenseReportId),
          "valueDate" -> toJson(line.valueDate),
          "account" -> toJson(line.account),
          "description" -> toJson(line.description),
          "expense" -> toJson(line.expense.amount),
          "expenseType" -> toJson(line.expense.qualifier)
        )
      )
    }
  }
}

object ExpenseReport {

  implicit object ExpenseReportReads extends Reads[ExpenseReport] {
    def reads(json: JsValue) = {
        ExpenseReport(
          (json \ "id").as[Pk[Long]],
          (json \ "startDate").as[Date],
          (json \ "endDate").as[Date],
          (json \ "userId").as[Pk[Long]],
          (json \ "lines").as[Seq[ExpenseLine]]
        )

    }
  }

  implicit object ExpenseReportWrites extends Writes[ExpenseReport] {
    def writes(report: ExpenseReport) = {
      toJson(
        Map(
          "id" -> toJson(report.id),
          "userId" -> toJson(report.userId),
          "startDate" -> toJson(report.from),
          "endDate" -> toJson(report.to),
          "lines" -> toJson(report.lines)
        )
      )
    }
  }

}


object Expense {

  implicit def tupleToExpense(tuple: (String, Double)):Expense = {
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


sealed trait Expense {
  val amount: Double
  val qualifier: String
}

case class Lodging(amount: Double) extends Expense    {
  val qualifier="Lodging"
}
case class Transportation(amount: Double) extends Expense {
  val qualifier="Transportation"
}

case class Gas(amount: Double) extends Expense {
  val qualifier="Gas"
}

case class Meal(amount: Double) extends Expense {
  val qualifier="Meal"
}

case class Phone(amount: Double) extends Expense {
  val qualifier="Phone"
}

case class Internet(amount: Double) extends Expense {
  val qualifier="Internet"
}

case class Other(amount: Double) extends Expense{
  val qualifier="Other"
}

