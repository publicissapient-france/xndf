package models

import java.util.Date
import anorm._
import play.api.libs.json._
import play.api.libs.json.Json._
import libs.json._

case class ExpenseReport(id: Pk[Long], from: Date, to: Date, userId: Pk[Long], _lines: Seq[ExpenseLine]) {
  lazy val lines = _lines

  def addLine(id: Pk[Long], valueDate: Date, evidenceNumber: Int, account: String, description: String, expense: Expense) = {
    lazy val newParent: ExpenseReport = ExpenseReport(this.id, this.from, this.to, this.userId, line +: this.lines)
    lazy val line: ExpenseLine = ExpenseLine(id, this.id, valueDate, evidenceNumber, account, description, expense)
    newParent
  }
}

case class ExpenseLine(id: Pk[Long],
                       expenseReportId: Pk[Long],
                       valueDate: Date,
                       evidenceNumber: Int,
                       account: String,
                       description: String,
                       expense: Expense) {
}

object ExpenseReport {

  implicit object ExpenseReportReads extends Reads[Pk[Long] => ExpenseReport] {
    def reads(json: JsValue) = {
      user: Pk[Long] =>
        val id: Pk[Long] = (json \ "id").as[Pk[Long]]
        val lines = ((json \ "lines") match {
          case JsArray(list) => list
          case _ => Seq()
        }).map(ExpenseLine.readsJson(_, id))

        ExpenseReport(
          id,
          (json \ "startDate").as[Date],
          (json \ "endDate").as[Date],
          user,
          lines
        )
    }
  }

  implicit object ExpenseReportWrites extends Writes[ExpenseReport] {
    def writes(report: ExpenseReport) = {
      toJson(
        Map(
          "id" -> toJson(report.id),
          "startDate" -> toJson(report.from),
          "endDate" -> toJson(report.to),
          "lines" -> toJson(report.lines.map(ExpenseLine.writesJson(_)))
        )
      )
    }
  }

}

object ExpenseLine {
  def readsJson(value: JsValue, reportId: Pk[Long]): ExpenseLine = {
    val qualifier = (value \ "expenseType").as[String]
    val amount = (value \ "expense").as[Double]



    ExpenseLine(
      (value \ "id").as[Pk[Long]],
      reportId,
      (value \ "valueDate").as[Date],
      (value \ "evidenceNumber").as[Int],
      (value \ "account").as[String],
      (value \ "description").as[String],
      ((value \ "expenseType").as[String],(value \ "expense").as[Double])
    )
  }

  def writesJson(line: ExpenseLine) = {
        toJson(
          Map(
            "id" -> toJson(line.id),
            "valueDate" -> toJson(line.valueDate),
            "evidenceNumber" -> toJson(line.evidenceNumber),
            "account" -> toJson(line.account),
            "description" -> toJson(line.description),
            "expense" -> toJson(line.expense.amount),
            "expenseType" -> toJson(line.expense.qualifier)
          )
        )
  }

}

object Expense {

  implicit def tupleToExpense(tuple: (String, Double)):Expense = {
    val (q, a) = tuple
    q match {
      case "Lodging" => Lodging(a)
      case "Transportation" => Transportation(a)
      case "Gas" => Gas(a)
      case "Meal" => Meal(a)
      case "Phone" => Phone(a)
      case "Internet" => Internet(a)
      case "Other" => Other(a)
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
  val qualifier="Phnone"
}

case class Internet(amount: Double) extends Expense {
  val qualifier="Internet"
}

case class Other(amount: Double) extends Expense{
  val qualifier="Other"
}

