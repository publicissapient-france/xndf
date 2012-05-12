package libs

import play.api.libs.json._
import com.mongodb.casbah._
import com.mongodb.casbah.commons.{MongoDBListBuilder, MongoDBObject}

package object mongo {

    implicit def toDb(jsObject: JsObject): DBObject = {
      jsObject.fields.map(field =>
        field match {
          case (key, JsArray(value: Seq[JsValue])) => MongoDBObject(key -> getValueForSeq(value))
          case (key, jsObject: JsObject) => MongoDBObject(key -> toDb(jsObject))
          case (key, jsValue: JsValue) => MongoDBObject(key -> getValue(jsValue))
        }
      ).reduce((left:DBObject, right:DBObject) => left ++ right )
    }

    private def folder(builder:MongoDBListBuilder)(value:JsValue)={
        value match {
          case JsArray(v) => builder+=getValueForSeq(v)
          case jso: JsObject => builder+=toDb(jso)
          case JsString(string) => builder+=string
          case JsBoolean(boolean)=> builder+=boolean
          case JsNumber(number) => builder+=number
          case JsNull => builder+=null
          case _ => builder
        }
    }

    private def getValueForSeq(seq: Seq[JsValue]): Seq[Any] = {
      val builder=MongoDBList.newBuilder
      seq.foldLeft(builder){
        (builder,value) =>  folder(builder)(value)
      }.result()
    }

    private def getValue(jsValue: JsValue) = {
      jsValue match {
        case JsNull => null
        case JsString(value) => value
        case JsBoolean(value) => value
        case JsNumber(value) => value
        case _ => throw new NoSuchMethodException("Unable to convert JsValue : " + jsValue.toString())
      }
    }


}
