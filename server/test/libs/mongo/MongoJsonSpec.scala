package libs.mongo

import org.specs2.mutable.Specification
import play.api.libs.json._
import com.mongodb.casbah.commons.{MongoDBList, MongoDBObject}

class MongoJsonSpec extends Specification {

  "A simple JSObject" should {
    "be converted to a MongoDBObject for a JsNumber" in {
      val jsonObject = JsObject(Seq("id"->JsNumber(12)))
      val mongoObject = MongoDBObject("id"->12)
      mongoObject mustEqual toDb(jsonObject)
    }
    "be converted to a MongoDBObject for a JsString" in {
      val jsonObject = JsObject(Seq("id"->JsString("string")))
      val mongoObject = MongoDBObject("id"->"string")
      mongoObject mustEqual toDb(jsonObject)
    }
    "be converted to a MongoDBObject for a JsBoolean" in {
      val jsonObject = JsObject(Seq("id"->JsBoolean(true)))
      val mongoObject = MongoDBObject("id"->true)
      mongoObject mustEqual toDb(jsonObject)
    }

    "be converted to a MongoDBObject for a complex object" in {
      val jsonObject = JsObject(Seq("id"->JsBoolean(true), "name"->JsString("toto")))
      val mongoObject = MongoDBObject("id"->true, "name"->"toto")
      mongoObject mustEqual toDb(jsonObject)
    }
    "be converted to a MongoDBObject for a JsBoolean" in {
      val jsonObject = JsObject(Seq("id"->JsBoolean(true)))
      val mongoObject = MongoDBObject("id"->true)
      mongoObject mustEqual toDb(jsonObject)
    }
    "be converted to a MongoDBObject for a JsArray" in {
      val jsonObject = JsObject(Seq("id"->JsArray(Seq(JsString("foo"),JsString("bar")))))
      val mongoObject = MongoDBObject("id"->MongoDBList("foo","bar"))

      mongoObject mustEqual toDb(jsonObject)
    }
  }
}
