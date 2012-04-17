package libs

import java.text.SimpleDateFormat
import java.util.Date
import play.api.libs.json._
import play.api.libs.json.Json._
import anorm.{NotAssigned, Id, Pk}

package object json{
  val ISO_8601_FORMATER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

  implicit object DateWrites extends Writes[Date] {
    def writes(d: Date) = JsString(ISO_8601_FORMATER.format(d))
  }

  implicit object DateReads extends Reads[Date] {

    def reads(json: JsValue) = {
        json match {
          case JsString(dateString) => ISO_8601_FORMATER.parse(dateString)
          case _ => throw new RuntimeException("iso8601-formated (yyyy-MM-dd'T'HH:mm:ssZ) string expected")
        }
    }
  }

  implicit object PkReads extends Reads[Pk[Long]] {
    def reads(json: JsValue) = {
      json match {
        case JsNumber(l) => Id(l.longValue())
        case _ => NotAssigned
      }
    }
  }

  implicit object PkWrites extends Writes[Pk[Long]] {
    def writes(pk: Pk[Long]): JsValue = {
      pk match {
        case Id(l) => toJson(l)
        case _ => JsUndefined("nil")
      }
    }
  }
}
