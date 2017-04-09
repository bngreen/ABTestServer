package utils

import java.{lang, util}

import play.api.libs.json._
/**
  * Created by Bruno on 4/9/2017.
  */
object Util {
  def json[T](obj: T)(implicit tjs: Writes[T]): JsValue = tjs.writes(obj)
  def toJavaMap[A, B](map:Map[A, B]) : java.util.Map[A, B] = {
    var jmap = new java.util.HashMap[A, B]()
    for(kv <- map){
      jmap.put(kv._1, kv._2)
    }
    jmap
  }

  def toJavaMap[A, B](map:Option[Map[A, B]]) : java.util.Map[A, B] = {
    map match {
      case Some(m) => toJavaMap(m)
      case None => new java.util.HashMap[A, B]()
    }
  }

  def toJavaMapD(map:Option[Map[String, Double]]) : java.util.Map[String, java.lang.Double] = {
    map match {
      case Some(m) => {
        var jmap = new java.util.HashMap[String, java.lang.Double]()
        for(kv <- m)
          jmap.put(kv._1, new lang.Double(kv._2))
        jmap
      }
      case None => new java.util.HashMap[String, java.lang.Double]()
    }
  }

}

