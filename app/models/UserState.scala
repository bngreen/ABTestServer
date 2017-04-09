package models

import play.api.libs.json.Json

/**
  * Created by Bruno on 4/9/2017.
  */
case class UserState(userid: String, experiments : Map[String, String])

object UserState{
  implicit val jsf = Json.format[UserState]
}

