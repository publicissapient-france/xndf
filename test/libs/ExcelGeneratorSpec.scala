package libs {

import org.specs2.mutable.Specification

class ExcelGeneratorSpec extends Specification {
  "ExcelGenerator" should {
    "know where to find the template" in {

    }
    "generate empty file for empty Expense" in {
      import models._
      import org.bson.types.ObjectId

      import libs.json.DATE_FORMATTER

      val date = DATE_FORMATTER.parse("2012-05-17T00:04:00+0200")
      val from = DATE_FORMATTER.parse("2012-05-01T00:04:00+0200")
      val to = DATE_FORMATTER.parse("2012-05-31T00:04:00+0200")
      val user = User(new ObjectId("222222222222222222222222"), "Mickey Mouse", "Mickey@mouse.com", "verifiedid")
      val expense = ExpenseReport(new ObjectId("111111111111111111111111"), date, date, user.id, Seq(),Some(ExpenseStatus.DRAFT))
      .addLine(date, "xebia", "Entrée Disneyland Paris!", Internet(1599.95),Seq())
      new ExcelGenerator().generate(user,expense).writeFile("ok.xls")
      0 === 0
    }
  }
}

}
