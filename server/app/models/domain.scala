package models

import java.util.Date
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

package object domain {
  def foo(e:Expense):String={
    e match {
      case Lodging => {"lodging"}
      case Transportation =>{"Transportation"}
    }
  }
}

case class ExpenseReport(id:Pk[Long], from:Date, to:Date, userId:Id[Long], lines:Seq[ExpenseLine]) {

}

case class ExpenseLine(id:Pk[Long],
                       expenseReport:ExpenseReport,
                       valueDate:Date,
                       evidenceNumber:Int,
                       account:String,
                       description:String,
                       expense:Expense) {

  implicit object ExpenseLineFormat extends Format[ExpenseLine] {

    def reads(json: JsValue): ExpenseLine = {
      ExpenseLine(NotAssigned,ExpenseReport(NotAssigned,new Date(), new Date(), Id(1l), Seq()),new Date(),1,"","",Lodging(1))
    }

    //unmarshaling to JSValue is covered in the next paragraph

    def writes(line: ExpenseLine): JsValue = JsObject(
      Seq("id" -> JsNumber(line.id.get))
    )
  }
}


sealed trait Expense {
  val amount:Double
  val qualifier:String
}

case class Lodging(amount:Double) extends Expense{
  val qualifier="Lodging"
}
case class Transportation(amount:Double) extends Expense{
  val qualifier="Transportation"
}
case class Gas(amount:Double) extends Expense{
  val qualifier="Gas"
}
case class Meal(amount:Double) extends Expense{
  val qualifier="Meal"
}
case class Phone(amount:Double) extends Expense{
  val qualifier="Phone"
}
case class Internet(amount:Double) extends Expense{
  val qualifier="Internet"
}
case class Other(amount:Double) extends Expense{
  val qualifier="Other"
}

