package libs

import java.text.SimpleDateFormat
import java.util.Date
import play.api.libs.json._
import play.api.libs.json.Json._

package object json {
  val ISO_8601_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
  val DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd")

  implicit object DateWrites extends Writes[Date] {
    def writes(d: Date) = JsString(DATE_FORMATTER.format(d))
  }

  implicit object DateReads extends Reads[Date] {

    def reads(json: JsValue): Date = {
      json match {
        case JsString(dateString) => DATE_FORMATTER.parse(dateString)
        case JsUndefined(error) => throw new RuntimeException("iso8601-formated (yyyy-MM-dd'T'HH:mm:ssZ) string expected, was JsUndefined : " + error)
        case _ => throw new RuntimeException("iso8601-formated (yyyy-MM-dd'T'HH:mm:ssZ) string expected, was " + json)
      }
    }
  }

}
