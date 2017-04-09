package utils

import play.api.libs.json._

/**
  * Created by Bruno on 4/9/2017.
  */
object Util {
  def json[T](obj: T)(implicit tjs: Writes[T]): JsValue = tjs.writes(obj)
}

