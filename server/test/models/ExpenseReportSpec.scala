package models

import org.specs2.mutable.Specification
import libs.json.DATE_FORMATTER
import models.ExpenseReport._
import play.api.libs.json.Json._
import org.bson.types.ObjectId

class ExpenseReportSpec extends Specification {
  "ExpenseReport model" should {
    "convert to and from Json" in {
      val date = DATE_FORMATTER.parse("2012-04-17T00:04:00+0200")

      val expenseReport = ExpenseReport(new ObjectId(), date, date, new ObjectId(), Seq())
        .addLine(date, "xebia", "description", Internet(15.99))
        .addLine(date, "xebia", "description", Internet(15.99))

      val jsExpenseReport = toJson(expenseReport)
      val toExpenseReport = jsExpenseReport.as[ExpenseReport]
      toExpenseReport === expenseReport
    }
    "convert to and from Json with NotAssigned" in {
      val date = DATE_FORMATTER.parse("2012-04-17T00:04:00+0200")

      val expenseReport = ExpenseReport(new ObjectId(), date, date, new ObjectId(), Seq())
        .addLine(date, "xebia", "description", Internet(15.99))
        .addLine(date, "xebia", "description", Internet(15.99))

      val jsExpenseReport = toJson(expenseReport)
      val toExpenseReport = jsExpenseReport.as[ExpenseReport]
      toExpenseReport === expenseReport
    }
  }
}
