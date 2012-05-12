package controllers

import play.api.mvc._
import play.api.libs.json.Json._
import java.util.Date
import models._
import anorm.{Pk, Id}

object ExpenseReportController extends Controller with Secured {
  val reportStore: Map[Pk[Long], Map[Pk[Long], ExpenseReport]] = Map(
    Id(1000l) ->
      Map(
        Id(1l) ->
          ExpenseReport(Id(1l), new Date(), new Date(), Id(1000), Seq(
            ExpenseLine(Id(1l), Id(1l), new Date(), 1, "Xebia", "Telephone", Phone(19.99)),
            ExpenseLine(Id(1l), Id(1l), new Date(), 1, "Xebia", "Internet", Internet(29.99))
          ))
        ,
        Id(2l) ->
          ExpenseReport(Id(2l), new Date(), new Date(), Id(1000), Seq(
            ExpenseLine(Id(1l), Id(1l), new Date(), 1, "Xebia", "Telephone", Lodging(95.00)),
            ExpenseLine(Id(1l), Id(1l), new Date(), 1, "Xebia", "Internet", Transportation(120.00))
          ))
      )
  )

  def index = IsAuthenticated {
    userId => implicit request =>
      val allReports = for {
        reports <- reportStore.values
        report <- reports.values
      } yield report
      Ok(toJson(allReports.toSeq))
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
}
