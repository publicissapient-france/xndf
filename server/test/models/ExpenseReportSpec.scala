package models

import org.specs2.mutable.Specification
import libs.json.ISO_8601_FORMATTER
import models.ExpenseReport._
import play.api.libs.json.Json._
import anorm.{NotAssigned, Id}

class ExpenseReportSpec extends Specification {
  "ExpenseReport model" should {
    "convert to and from Json" in {
      val date = ISO_8601_FORMATTER.parse("2012-04-17T00:04:00+0200")

      val expenseReport = ExpenseReport(Id(1), date, date, Id(1), Seq())
        .addLine(NotAssigned, date, "xebia", "description", Internet(15.99))
        .addLine(NotAssigned, date, "xebia", "description", Internet(15.99))

      val jsExpenseReport = toJson(expenseReport)
      val toExpenseReport = jsExpenseReport.as[ExpenseReport]
      toExpenseReport === expenseReport
    }
  }
}
