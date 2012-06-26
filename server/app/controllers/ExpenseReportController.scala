package controllers

import play.api.mvc._
import play.api.libs.json.Json._
import java.util.Date
import models._
import org.bson.types.ObjectId

object ExpenseReportController extends Controller with Secured {
  val reportStore: Map[ObjectId, Map[ObjectId, ExpenseReport]] = Map(
    new ObjectId("1000") ->
      Map(
        new ObjectId("1l") ->
          ExpenseReport(new ObjectId("1l"), new Date(), new Date(), new ObjectId("1000"), Seq(
            ExpenseLine(new Date(), "Xebia", "Telephone", Phone(19.99)),
            ExpenseLine(new Date(), "Xebia", "Internet", Internet(29.99))
          ))
        ,
        new ObjectId("2l") ->
          ExpenseReport(new ObjectId("2l"), new Date(), new Date(), new ObjectId("1000"), Seq(
            ExpenseLine(new Date(), "Xebia", "Telephone", Lodging(95.00)),
            ExpenseLine(new Date(), "Xebia", "Internet", Transportation(120.00))
          ))
      ),
      new ObjectId("1001") ->
      Map(
        new ObjectId("3l") ->
          ExpenseReport(new ObjectId("3l"), new Date(), new Date(), new ObjectId("1000"), Seq(
            ExpenseLine(new Date(), "Xebia", "Free", Phone(19.99)),
            ExpenseLine(new Date(), "Xebia", "ADSL", Internet(29.99))
          ))
        ,
        new ObjectId("4l") ->
          ExpenseReport(new ObjectId("4l"), new Date(), new Date(), new ObjectId("1000"), Seq(
            ExpenseLine(new Date(), "Xebia", "Logement", Lodging(95.00)),
            ExpenseLine(new Date(), "Xebia", "Transport", Transportation(120.00))
          ))
      )
  )

  def index = IsAuthenticated {
    userId => implicit request =>
      val allReports = for {
        user <- User.findByVerifiedId(userId)
        reports <- reportStore.get(user.id)
      } yield reports.values
      Ok(toJson(allReports.flatten.toSeq))
  }

  def show(id: String) = IsAuthenticated {
    userId => implicit request =>
      val report = for {
        user <- User.findByVerifiedId(userId)
        reports <- reportStore.get(user.id)
        report <- reports.get(new ObjectId(id))
      } yield report
      Ok(toJson(report))
  }

  def create= IsAuthenticated(parse.json){ userId=> implicit request =>
      val jsReport=request.body
      val expenseReport=jsReport.as[ExpenseReport]
      val idUser: ObjectId = User.findByVerifiedId(userId).map(_.id).getOrElse(null)
      val saved: ExpenseReport = ExpenseReport(new ObjectId(), expenseReport.from, expenseReport.to, idUser, expenseReport.lines)


      Ok(toJson(saved))
  }
}
