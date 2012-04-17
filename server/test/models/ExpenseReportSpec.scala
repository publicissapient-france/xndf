package models

import org.specs2.mutable.Specification
import java.util.Date
import libs.json.ISO_8601_FORMATER
import models.ExpenseReport._
import play.api.libs.json.Json._
import anorm.{Pk, NotAssigned, Id}

/**
 * User: Jean Helou
 * Date: 11/04/12
 */

class ExpenseReportSpec extends Specification{
  "ExpenseReport model" should {
    "convert to and from Json" in {
      val date=ISO_8601_FORMATER.parse("2012-04-17T00:04:00+0200")

      val expenseReport=ExpenseReport(Id(1), date, date,Id(1), Seq() ).addLine(NotAssigned, date, 1,"xebia","description",Internet(15.99))
      val jsExpenseReport=toJson(expenseReport)
      val user=Id(1l)
      val toExpenseReport = jsExpenseReport.as[Pk[Long]=>ExpenseReport]
      val readExpenseReport = toExpenseReport(user)

      readExpenseReport === expenseReport
    }
  }
}
