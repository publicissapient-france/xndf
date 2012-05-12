package libs.json

import org.specs2.mutable.Specification

import play.api.libs.json.Json._
import java.util.Date
import anorm.{Id, NotAssigned, Pk}

class CustomConvertersSpec extends Specification {

  "implicit conversion for Date" should {
    "convert a date to and from json " in {
      //iso8601 only has second precision
      //new Date has millisecond precision which makes the test fail
      val date = ISO_8601_FORMATTER.parse("2012-04-17T00:04:00+0200")
      val readDate = toJson(date).as[Date]
      readDate === date
    }
  }

  "implicit conversion for Pk[Long]" should {
    "convert NotAssigned to and from json " in {
      val id: Pk[Long] = NotAssigned
      val readId = toJson(id).as[Pk[Long]]
      readId === id
    }

    "convert Id(1) to and from json" in {
      val id: Pk[Long] = Id(1)
      val readId = toJson(id).as[Pk[Long]]
      readId === id
    }
  }

}
