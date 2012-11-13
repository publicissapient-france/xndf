package libs {

import org.specs2.mutable.Specification
import org.apache.poi.ss.usermodel._

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
      .addLine(date, "xebia", "TEST TEST Internet ", Internet(1599.95),Seq())
      .addLine(date, "xebia", "TEST TEST Phone", Phone(5099.95),Seq())
      .addLine(date, "xebia", "TEST TEST Meal", Meal(5099.95),Seq())
      .addLine(date, "xebia", "TEST TEST Lodging", Lodging(5099.95),Seq())
      .addLine(date, "xebia", "TEST TEST Gas", Gas(5099.95),Seq())
      .addLine(date, "xebia", "TEST TEST Other", Other(5099.95),Seq())
      .addLine(date, "xebia", "TEST TEST Transportation", Transportation(5099.95),Seq())
      val generator=new ExcelGenerator()
      val workbook: Workbook=generator.generate(user,expense).workBook
      val sheet: Sheet = workbook.getSheet("Note de frais")
      generator.writeFile("ok.xlsx")
      sheet.getRow(17).getCell(6).getStringCellValue === "TEST TEST Internet "
      sheet.getRow(17).getCell(12).getNumericCellValue === 1599.95
      sheet.getRow(18).getCell(6).getStringCellValue === "TEST TEST Phone"
      sheet.getRow(18).getCell(11).getNumericCellValue === 5099.95

      sheet.getRow(26).getCell(7).getNumericCellValue === 5099.95
      sheet.getRow(26).getCell(8).getNumericCellValue === 5099.95
      sheet.getRow(26).getCell(9).getNumericCellValue === 5099.95
      sheet.getRow(26).getCell(10).getNumericCellValue === 5099.95
      sheet.getRow(26).getCell(11).getNumericCellValue === 5099.95
      sheet.getRow(26).getCell(12).getNumericCellValue === 1599.95
      sheet.getRow(26).getCell(13).getNumericCellValue === 5099.95

      sheet.getRow(26).getCell(14).getNumericCellValue === 32199.65
      sheet.getRow(27).getCell(14).getNumericCellValue === 32199.65
      sheet.getRow(29).getCell(14).getNumericCellValue === 32199.65


    }
  }
}
}