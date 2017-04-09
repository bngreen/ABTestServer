package models

import java.util.UUID

/**
  * Created by Bruno on 4/9/2017.
  */
case class Variation(id: UUID, name:String, description:String, rate:Double=0.5)
