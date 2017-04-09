package models

import java.util.UUID

/**
  * Created by Bruno on 4/9/2017.
  */
case class Experiment(id:UUID, starttime : Long, name:String, description:String, variations:Seq[Variation])
