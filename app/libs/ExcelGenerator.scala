package libs {

import java.io.ByteArrayOutputStream

class ExcelGenerator {

  import java.io.File
  import java.io.FileOutputStream
  import org.apache.poi.ss.usermodel._
  import org.apache.poi.xssf.usermodel._
  import models._
  import ExcelGenerator._

  lazy val workBook:Workbook = new XSSFWorkbook(getClass.getResourceAsStream(ExcelGenerator.TEMPLATE_PATH))


  def writeTotal(total: Double, cell: Cell){
    cell.setCellFormula(null)
    cell.setCellValue(total)
  }

  def generate(user: User, expenseReport: ExpenseReport) = {
    val sheet: Sheet = workBook.getSheet("Note de frais")
    sheet.getRow(11).getCell(5).setCellValue(user.name)
    sheet.getRow(11).getCell(14).setCellValue(expenseReport.from)
    sheet.getRow(13).getCell(14).setCellValue(expenseReport.to)
    writeSubtotals(expenseReport.subtotals, sheet.getRow(SUBTOTALS_ROW_INDEX))
    writeTotal(expenseReport.subtotals.map(_.amount).sum, sheet.getRow(SUBTOTALS_ROW_INDEX).getCell(TOTAL_COL_INDEX))
    writeTotal(expenseReport.subtotals.map(_.amount).sum, sheet.getRow(SUBTOTAL_ROW_INDEX).getCell(TOTAL_COL_INDEX))
    writeTotal(expenseReport.subtotals.map(_.amount).sum, sheet.getRow(TOTAL_ROW_INDEX).getCell(TOTAL_COL_INDEX))
    expenseReport.lines.zipWithIndex.foldLeft(sheet)(lineToRow(_, _))
    this
  }
  def writeSubtotals(expenses: Seq[Expense], row: Row){
    expenses.map(writeExpenseToCell(_,row))
  }

  def writeExpenseToCell(expense: Expense, row: Row) {
    val cell=expense match {
      case Lodging(amount) => row.getCell(LODGING_COL_INDEX)
      case Transportation(amount) => row.getCell(TRANSPORTATION_COL_INDEX)
      case Gas(amount) => row.getCell(GAS_COL_INDEX)
      case Meal(amount) => row.getCell(MEAL_COL_INDEX)
      case Phone(amount) => row.getCell(PHONE_COL_INDEX)
      case Internet(amount) => row.getCell(INTERNET_COL_INDEX)
      case Other(amount) => row.getCell(OTHER_COL_INDEX)
    }
    cell.setCellFormula(null)
    cell.setCellValue(expense.amount)

  }

  def lineToRow(sheet: Sheet,lineWithIndex: (ExpenseLine, Int)) = {
    val index = lineWithIndex._2
    val line = lineWithIndex._1
    val templateRow = sheet.getRow(TEMPLATE_ROW_INDEX)
    val row: Row = createRow(sheet, templateRow)
    row.getCell(3).setCellValue(line.valueDate)
    row.getCell(4).setCellValue(index)
    row.getCell(5).setCellValue(line.account)
    row.getCell(6).setCellValue(line.description)
    writeExpenseToCell(line.expense,row)

    row.getCell(SUBTOTAL_COL_INDEX).setCellValue(line.expense.amount)
    sheet
  }

  def createRow(sheet: Sheet, templateRow: Row): Row = {
    sheet.shiftRows(INSERT_ROW_INDEX,sheet.getLastRowNum, 1, true, false)
    val row: Row = sheet.createRow(INSERT_ROW_INDEX)
    (FIRST_COL_INDEX until LAST_COL_INDEX).map({
      col =>
        createCell(row, col, templateRow)
    })
    row
  }

  def createCell(row: Row, col: Int, templateRow: Row): Cell = {
    val cell: Cell = row.createCell(col)
    cell.setCellStyle(templateRow.getCell(col).getCellStyle)
    cell
  }

  def writeFile(path: String) {
    val file = new File(path)
    val fileOut = new FileOutputStream(file)
    //workBook.setForceFormulaRecalculation(true)
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
  val TEMPLATE_PATH = "/public/template.xlsx"
  val TEMPLATE_ROW_INDEX = 16
  val INSERT_ROW_INDEX   = 17
  val SUBTOTALS_ROW_INDEX = 19
  val SUBTOTAL_ROW_INDEX = 20
  val TOTAL_ROW_INDEX = 22

  val FIRST_COL_INDEX=0
  val LAST_COL_INDEX=17

  val LODGING_COL_INDEX=7
  val TRANSPORTATION_COL_INDEX=8
  val GAS_COL_INDEX=9
  val MEAL_COL_INDEX=10
  val PHONE_COL_INDEX=11
  val INTERNET_COL_INDEX=12
  val OTHER_COL_INDEX=13
  val SUBTOTAL_COL_INDEX=14
  val TOTAL_COL_INDEX=14
}

}