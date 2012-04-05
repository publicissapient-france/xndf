package models

import java.util.Date
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

/**
 * @author David Galichet.
 */

package object domain {

}

case class ExpenseReport(id:Pk[Long], number:String, from:Date, to:Date, userId:Id[Long]) {

}

case class ExpenseLine(id:Pk[Long],
                       expenseReportId:List[Id[Long]],
                       creationDate:Date,
                       evidenceNumber:Int,
                       account:String,
                       description:String,
                       expense:Expense) {

}


sealed trait Expense {
  val amount:Double
}

case class Lodging(amount:Double) extends Expense
case class Transportation(amount:Double) extends Expense
case class Gas(amount:Double) extends Expense
case class Meal(amount:Double) extends Expense
case class Phone(amount:Double) extends Expense
case class Internet(amount:Double) extends Expense
case class Other(amount:Double) extends Expense

