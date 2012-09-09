package controllers

import play.api.mvc._
import play.api.Logger
import play.api.libs.json.Json._
import models._
import models.ExpenseFormat._
import org.bson.types.ObjectId
import play.api.libs.json.JsValue
import fly.play.ses._
import javax.mail.Message
import ch.qos.logback.classic.spi.LoggerRemoteView
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.MultipartFormData.FilePart
import fly.play.ses.EmailAddress
import fly.play.ses.Email
import fly.play.ses.Recipient
import libs.ExcelGenerator

object ExpenseReportController extends Controller with Secured {

  def index = IsAuthenticated {
    userId => implicit request =>

      val user: Option[User] = User.findByEmail(userId)
      user.map({
        user =>
          val expenseReports: List[ExpenseReport] = ExpenseReport.findAllByUserId(user.id)
          Ok(toJson(expenseReports))
      }).getOrElse(BadRequest("fail"))
  }

  def show(id: String) = IsAuthenticated {
    userId => implicit request =>
      val result = for {
        user <- User.findByEmail(userId)
        expenseReport <- ExpenseReport.findByIdAndUserID(new ObjectId(id), user.id)
      } yield Ok(toJson(expenseReport))
      result.getOrElse(BadRequest("fail"))
  }

  def create = IsAuthenticated(parse.json) {
    userId => implicit request =>
      val jsReport = request.body
      User.findByEmail(userId).map {
        user =>
          val toExpenseReport = jsReport.as[User => ExpenseReport]
          val expenseReport = toExpenseReport(user)
          expenseReport.save()
          Ok(toJson(expenseReport))
      }.getOrElse(
        Redirect(routes.Application.login())
      )
  }
  def update(id: String) = IsAuthenticated(parse.json) {
    userId => implicit request =>
      val jsReport: JsValue = request.body
      User.findByEmail(userId).map {
        user =>
          val toExpenseReport = jsReport.as[User => ExpenseReport]
          val expenseReport = toExpenseReport(user)
          expenseReport.save()
          expenseReport.status.map( _ match { case ExpenseStatus.SUBMITTED=> emailXebia (expenseReport,user); case _ => Unit })
          Ok (toJson(expenseReport))
      }.getOrElse(
        Redirect(routes.Application.login())
      )
  }
  def emailXebia(expenseReport:ExpenseReport,user:User){
    implicit def filepart2Attachment(filePart:FilePart[Array[Byte]])={
      Attachment(filePart.filename, filePart.ref,filePart.contentType.getOrElse("application/octet-stream"),Disposition.Attachment)
    }
//    impossible de relire les fichiers ecrits dans mongo pour l'instant !!
//    val files: Seq[Attachment] = expenseReport.lines.flatMap({
//      line => line.evidences.map(Evidence.findById(_))
//    }).map(filepart2Attachment(_))
    val report=Attachment("report.xls", new ExcelGenerator().generate(user,expenseReport).asByteArray(),"application/excel")
    val email:Email=Email(subject="Note de frais",
          from=EmailAddress(user.name,user.email),
          replyTo=None,
          recipients=Seq(Recipient(Message.RecipientType.TO ,EmailAddress(user.name,user.email))),
          text="Bonjour, ma note de frais et ses justificatifs ci-joints...",
          htmlText="Bonjour, ma note de frais et ses justificatifs ci-joints...",
          attachments=Seq(report)
    )
    Ses.sendEmail(email = email)
    Logger.info("sent email:\n"+email)
  }
}
