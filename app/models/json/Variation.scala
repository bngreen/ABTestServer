package models.json

import play.api.libs.json.Json

/**
  * Created by Bruno on 4/9/2017.
  */
case class Variation(name:String, description:String, rate:Double)

object Variation{
  implicit val jsf = Json.format[Variation]
  def create(v: models.Variation) = models.json.Variation(v.name, v.description, v.rate)
}
