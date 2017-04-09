package utils

import java.util

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

}

