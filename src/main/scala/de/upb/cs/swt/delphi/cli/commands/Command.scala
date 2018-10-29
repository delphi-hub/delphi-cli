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
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import de.upb.cs.swt.delphi.cli.Config

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Represents the implementation of a command of the CLI
  */
trait Command {

  /**
    * Executes the command implementation
    * @param config The current configuration for the command
    */
  def execute(config: Config)(implicit system : ActorSystem): Unit

  /**
    * Implements a common request type using currying to avoid code duplication
    * @param target The endpoint to perform a Get request on
    * @param config The current configuration for the command
    */
  protected def executeGet(target: String, parameters: Map[String, String] = Map())(config: Config, system : ActorSystem) : Option[String] = {
    implicit val sys : ActorSystem = system
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = sys.dispatcher

    val uri = Uri(config.server)
    config.consoleOutput.outputInformation(s"Contacting server ${uri}...")

    val responseFuture = Http().singleRequest(HttpRequest(uri = uri.withPath(uri.path + target).withQuery(Query(parameters))))

    responseFuture.onComplete {
      case Failure(_) => println(s"Could not reach server ${config.server}.")
      case _ =>
    }

    val result = Await.result(responseFuture, 30 seconds)
    val resultString = result match {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        entity.dataBytes.runFold(ByteString(""))(_ ++ _).map { body =>
          Some(body.utf8String)
        }
      case resp @ HttpResponse(code, _, _, _) => {
        outputError(config)("Artifact not found.")
        resp.discardEntityBytes()
        Future(None)
      }
    }

    Await.result(resultString, Duration.Inf)
  }

  protected def outputInformation(implicit config: Config): String => Unit = config.consoleOutput.outputInformation _
  protected def outputResult(implicit config: Config): Any => Unit = config.consoleOutput.outputResult _
  protected def outputError(implicit config: Config): String => Unit = config.consoleOutput.outputError _

}
