package libs.json

import org.specs2.mutable.Specification

import play.api.libs.json.Json._
import java.util.Date
import anorm.{Id, NotAssigned, Pk}

class CustomConvertersSpec extends Specification{

  "implicit conversion for Date" should {
    "convert a date to and from json " in {
      //don't use new Date, it has millisecond precision
      //iso8601 only has second precision
      val date=ISO_8601_FORMATER.parse("2012-04-17T00:04:00+0200")
      val readDate=toJson(date).as[Date]
      readDate must beEqualTo(date)
    }
  }

  "implicit conversion for Pk[Long]" should {
    "convert NotAssigned to and from json " in {
      val id:Pk[Long]=NotAssigned
      val readId=toJson(id).as[Pk[Long]]
      readId must beEqualTo(id)
    }

    "convert Id(1) to and from json" in {
      val id:Pk[Long]=Id(1)
      val readId=toJson(id).as[Pk[Long]]
      readId must beEqualTo(id)
    }
  }

}
