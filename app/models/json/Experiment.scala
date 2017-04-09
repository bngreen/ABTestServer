package models.json

import play.api.libs.json.Json

/**
  * Created by Bruno on 4/9/2017.
  */
case class Experiment(starttime:Long, name:String, description:String, variations:Seq[Variation])

object Experiment{
  implicit val jsf = Json.format[Experiment]
}
