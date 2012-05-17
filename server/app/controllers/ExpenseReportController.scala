package controllers

import play.api.mvc._
import play.api.libs.json.Json._
import java.util.Date
import models._
import play.api.libs.json.JsValue
import anorm.{NotAssigned, Pk, Id}
import org.specs2.internal.scalaz.Digit._0

object ExpenseReportController extends Controller with Secured {
  val reportStore: Map[Pk[Long], Map[Pk[Long], ExpenseReport]] = Map(
    Id(1000l) ->
      Map(
        Id(1l) ->
          ExpenseReport(Id(1l), new Date(), new Date(), Id(1000), Seq(
            ExpenseLine(Id(1l), Id(1l), new Date(), "Xebia", "Telephone", Phone(19.99)),
            ExpenseLine(Id(2l), Id(1l), new Date(), "Xebia", "Internet", Internet(29.99))
          ))
        ,
        Id(2l) ->
          ExpenseReport(Id(2l), new Date(), new Date(), Id(1000), Seq(
            ExpenseLine(Id(1l), Id(2l), new Date(), "Xebia", "Telephone", Lodging(95.00)),
            ExpenseLine(Id(2l), Id(2l), new Date(), "Xebia", "Internet", Transportation(120.00))
          ))
      ),
      Id(1001l) ->
      Map(
        Id(3l) ->
          ExpenseReport(Id(3l), new Date(), new Date(), Id(1000), Seq(
            ExpenseLine(Id(1l), Id(3l), new Date(), "Xebia", "Free", Phone(19.99)),
            ExpenseLine(Id(2l), Id(3l), new Date(), "Xebia", "ADSL", Internet(29.99))
          ))
        ,
        Id(4l) ->
          ExpenseReport(Id(4l), new Date(), new Date(), Id(1000), Seq(
            ExpenseLine(Id(1l), Id(4l), new Date(), "Xebia", "Logement", Lodging(95.00)),
            ExpenseLine(Id(2l), Id(4l), new Date(), "Xebia", "Transport", Transportation(120.00))
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

  def show(id: Long) = IsAuthenticated {
    userId => implicit request =>
      val report = for {
        user <- User.findByVerifiedId(userId)
        reports <- reportStore.get(user.id)
        report <- reports.get(Id(id))
      } yield report
      Ok(toJson(report))
  }

  def create= IsAuthenticated(parse.json){ userId=> implicit request =>
      val jsReport=request.body
      val expenseReport=jsReport.as[ExpenseReport]
      val idUser: Pk[Long] = User.findByVerifiedId(userId).map(_.id).getOrElse(NotAssigned)
      val saved: ExpenseReport = ExpenseReport(Id(new Date().getTime), expenseReport.from, expenseReport.to, idUser, expenseReport.lines)


      Ok(toJson(saved))
  }
}
