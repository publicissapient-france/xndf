package libs {

import java.io.ByteArrayOutputStream

class ExcelGenerator {

  import java.io.File
  import java.io.FileOutputStream
  import org.apache.poi.hssf.usermodel._
  import models._

  lazy val workBook = new HSSFWorkbook(getClass.getResourceAsStream(ExcelGenerator.TEMPLATE_PATH))

  def generate(user: User, expenseReport: ExpenseReport) = {
    val sheet: HSSFSheet = workBook.getSheet("Note de frais")
    sheet.getRow(11).getCell(5).setCellValue(user.name)
    sheet.getRow(11).getCell(14).setCellValue(expenseReport.from)
    sheet.getRow(13).getCell(14).setCellValue(expenseReport.to)
    lazy val indexes: Stream[Int] = {
      def loop(h: Int): Stream[Int] = h #:: loop(h + 1)
      loop(1)
    }
    expenseReport.lines.zip(indexes).foldRight(sheet)(lineToRow(_, _))
    this
  }

  def lineToRow(lineWithIndex: (ExpenseLine, Int), sheet: HSSFSheet) = {
    val index = lineWithIndex._2
    val line = lineWithIndex._1
    val templateRow = sheet.getRow(ExcelGenerator.TEMPLATE_ROW_AT)
    val row: HSSFRow = createRow(sheet, templateRow)
    row.getCell(3).setCellValue(line.valueDate)
    row.getCell(4).setCellValue(index)
    row.getCell(5).setCellValue("XEBIA")
    row.getCell(6).setCellValue(line.description)
    val cell: HSSFCell = line.expense match {
      case Lodging(amount) => row.getCell(7)
      case Transportation(amount) => row.getCell(8)
      case Gas(amount) => row.getCell(9)
      case Meal(amount) => row.getCell(10)
      case Phone(amount) => row.getCell(11)
      case Internet(amount) => row.getCell(12)
      case Other(amount) => row.getCell(13)
    }
    cell.setCellValue(line.expense.amount)
    row.getCell(14).setCellValue(line.expense.amount)
    sheet
  }

  def createRow(sheet: HSSFSheet, templateRow: HSSFRow): HSSFRow = {
    val row: HSSFRow = sheet.createRow(ExcelGenerator.INSERT_ROW_AT)
    (0 until 17).map({
      col =>
        createCell(row, col, templateRow)
    })
    row
  }

  def createCell(row: HSSFRow, col: Int, templateRow: HSSFRow): HSSFCell = {
    val cell: HSSFCell = row.createCell(col)
    cell.setCellStyle(templateRow.getCell(col).getCellStyle)
    cell
  }

  def writeFile(path: String) {
    val file = new File(path)
    val fileOut = new FileOutputStream(file)
    workBook.write(fileOut)
    fileOut.close()
  }
  def asByteArray()={
    val outputStream = new ByteArrayOutputStream()
    workBook.write(outputStream)
    outputStream.toByteArray
  }
}

object ExcelGenerator {
  val TEMPLATE_PATH = "/public/template.xls"
  val TEMPLATE_ROW_AT = 16
  val INSERT_ROW_AT = 17
}

}