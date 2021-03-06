/*
Copyright (C) 2017  Bruno Naspolini Green. All rights reserved.

This file is part of ABTestServer.

ABTestServer is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

ABTestServer is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with ABTestServer.  If not, see <http://www.gnu.org/licenses/>.
*/

package controllers

import javax.inject.Inject

import models.ExperimentCassRepository
import models.json.Variation
import play.api._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import utils.Util._

import scala.util.Random

/**
  * Created by Bruno on 4/9/2017.
  */
class ExperimentController @Inject()(experimentCassRepository: ExperimentCassRepository) extends Controller{
    def createExperiment = Action.async(parse.json[models.json.Experiment]) { implicit request =>
      val experiment = request.body
      val totalRate = experiment.variations.map(_.rate).sum
      if (totalRate < 0.9999 || totalRate > 1.0099)
        Future(BadRequest("Variations rates should add up to 1"))
      else
        for{
          exists <- experimentCassRepository.exists(experiment.name)
          uuid <- if (exists) Future(None) else experimentCassRepository.createExperiment(experiment).map(x=>Some(x))
        } yield uuid match {
          case Some(x) => Ok("created: " + x.toString)
          case None => BadRequest("already exists")
        }
    }


    def getExperiment(name:String) = Action.async {
      experimentCassRepository.getByName(name).map( {
        case Some(x) => Ok(json(models.json.Experiment(x.starttime, x.name, x.description, x.variations.map(v => models.json.Variation(v.name, v.description, v.rate)))))
        case None => NotFound
      })
    }

    private val random = new Random()

    private def chooseVariation(variations : Seq[models.Variation]) : models.Variation = {
      val n = random.nextDouble()
      var sum = 0.0
      for(v <- variations){
        sum = sum + v.rate
        if(n < sum)
          return v
      }
      throw new Exception()//this should not happen
    }

    def activateExperiment(name:String) = Action.async(parse.json[models.UserState]) { implicit request =>
      val userState = request.body
      experimentCassRepository.getByName(name).map( {
        case Some(x) => {
          val newState = models.UserState(userState.userid, userState.experiments + ( name -> userState.experiments.getOrElse(name, chooseVariation(x.variations).name)))
          Ok(json(newState))
          //Ok(json(Variation.create(chooseVariation(x.variations))))
        }
        case None => NotFound
      })
    }

}
