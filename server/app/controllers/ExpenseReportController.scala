package controllers

import play.api.mvc._
import play.api.libs.json.Json._
import models._
import models.ExpenseFormat._
import org.bson.types.ObjectId

object ExpenseReportController extends Controller with Secured {

  def index = IsAuthenticated {
    userId => implicit request =>

      val user: Option[User] = User.findByVerifiedId(userId)
      user.map({
        user =>
          val expenseReports: List[ExpenseReport] = ExpenseReport.findAllByUserId(user.id)
          Ok(toJson(expenseReports))
      }).getOrElse(BadRequest("fail"))
  }

  def show(id: String) = IsAuthenticated {
    userId => implicit request =>
      val result = for {
        user <- User.findByVerifiedId(userId)
        expenseReport <- ExpenseReport.findByIdAndUserID(new ObjectId(id), user.id)
      } yield Ok(toJson(expenseReport))
      result.getOrElse(BadRequest("fail"))
  }

  def create = IsAuthenticated(parse.json) {
    userId => implicit request =>
      val jsReport = request.body
      User.findByVerifiedId(userId).map {
        user =>
          val toExpenseReport = jsReport.as[User => ExpenseReport]
          val expenseReport = toExpenseReport(user)
          expenseReport.save
          Ok(toJson(expenseReport))
      }.getOrElse(
        Redirect(routes.Application.login())
      )
  }
}
