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

package models

import javax.inject.{Inject, Singleton}

import com.datastax.driver.core.Cluster
import play.api.{Configuration, Play}


/**
  * Created by Bruno on 4/9/2017.
  */
@Singleton
class SimpleCassClient @Inject()(configuration: Configuration) {

    private val cluster = Cluster.builder().addContactPoint(configuration.getString("cassandra.defaultnode").get).build()
    val session = cluster.connect()
    def createSchema(): Unit = {
      session.execute("CREATE KEYSPACE IF NOT EXISTS abtest WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};")
      session.execute(
        """CREATE TABLE IF NOT EXISTS abtest.experiment (
          id uuid PRIMARY KEY,
          starttime timestamp,
          name text,
          description text,
          );""")
      session.execute(
        """CREATE INDEX IF NOT EXISTS experiment_name ON abtest.experiment (name);"""
      )
      session.execute(
        """CREATE TABLE IF NOT EXISTS abtest.variation (
          id uuid PRIMARY KEY,
          experiment_id uuid,
          name text,
          description text,
          rate double,
          );""")
      session.execute(
        """CREATE INDEX IF NOT EXISTS variation_name ON abtest.variation (name);"""
      )
      session.execute(
        """CREATE INDEX IF NOT EXISTS variation_experiment_id ON abtest.variation (experiment_id);"""
      )
      session.execute(
        """CREATE TABLE IF NOT EXISTS abtest.event (
          time timestamp,
          userid text,
          name text,
          metrics map<text, text>,
          nmetrics map<text, double>,
          experiments map<text, text>,
          PRIMARY KEY (name, time, userid)
          );""")
      session.execute(
        """CREATE INDEX IF NOT EXISTS event_metric_key ON abtest.event (KEYS(metrics));"""
      )
      session.execute(
        """CREATE INDEX IF NOT EXISTS event_nmetric_key ON abtest.event (KEYS(nmetrics));"""
      )
      session.execute(
        """CREATE INDEX IF NOT EXISTS event_experiment_key ON abtest.event (KEYS(experiments));"""
      )
    }
  createSchema()
}
