// Copyright (C) 2018 The Delphi Team.
// See the LICENCE file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package de.upb.cs.swt.delphi.cli.commands

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import de.upb.cs.swt.delphi.cli.Config
import de.upb.cs.swt.delphi.cli.artifacts.RetrieveResult
import de.upb.cs.swt.delphi.cli.artifacts.SearchResultJson._
import de.upb.cs.swt.delphi.cli.commands.SearchCommand.information
import spray.json.DefaultJsonProtocol

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.Source
import scala.util.{Failure, Success}

/**
  * The implementation of the retrieve command.
  * Retrieves the contents of the file at the endpoint specified by the config file, and prints them to stdout
  */
object RetrieveCommand extends Command with SprayJsonSupport with DefaultJsonProtocol {


   def execute(config: Config)(implicit system: ActorSystem): Unit = {
    implicit val ec = system.dispatcher
    implicit val materializer = ActorMaterializer()

    //Checks whether the ID should be loaded from a file or not, and either returns the first line
    //  of the given file if it is, or the specified ID otherwise
    def checkTarget: String = {
      if (config.opts.contains("file")) {
        val source = Source.fromFile(config.args.head)
        val target = source.getLines.next()
        source.close()
        target
      } else {
        config.id
      }
    }

    val result = executeGet(
      s"/retrieve/$checkTarget",
      Map("pretty" -> "")
    )(config, system)

    result.map(s => {
      if (config.raw) {
        reportResult(config)(s)
      }

      if (!config.raw || !config.csv.equals("")) {
        val unmarshalledFuture = Unmarshal(s).to[List[RetrieveResult]]

        unmarshalledFuture.transform {
          case Success(unmarshalled) => {
            val unmarshalled = Await.result(unmarshalledFuture, Duration.Inf)
            success(config)(s"Found ${unmarshalled.size} item(s).")
            reportResult(config)(unmarshalled)

            if(!config.csv.equals("")) {
              exportResult(config)(unmarshalled)
              information(config)("Results written to file '" + config.csv + "'")
            }

            Success(unmarshalled)
          }
          case Failure(e) => {
            error(config)(s)
            Failure(e)
          }
        }
      }
    })
  }
}
