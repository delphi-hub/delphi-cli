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
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.util.ByteString
import de.upb.cs.swt.delphi.cli.Config
import de.upb.cs.swt.delphi.cli.artifacts.SearchResult
import de.upb.cs.swt.delphi.cli.artifacts.SearchResultJson._
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object SearchCommand extends Command with SprayJsonSupport with DefaultJsonProtocol {
  /**
    * Executes the command implementation
    *
    * @param config The current configuration for the command
    */
  override def execute(config: Config)(implicit system: ActorSystem): Unit = {
    implicit val ec = system.dispatcher
    implicit val materializer = ActorMaterializer()

    def query = config.args.head
    outputInformation(config)(s"Searching for artifacts matching <$query>.")
    implicit val queryFormat = jsonFormat2(Query)

    val baseUri = Uri(config.server)
    val prettyParam = Map("pretty" -> "")
    val searchUri = baseUri.withPath(baseUri.path + "/search").withQuery(akka.http.scaladsl.model.Uri.Query(prettyParam))
    val responseFuture = Marshal(Query(query)).to[RequestEntity] flatMap { entity =>
      Http().singleRequest(HttpRequest(uri = searchUri, method = HttpMethods.POST, entity = entity))
    }

    val response = Await.result(responseFuture, 10 seconds)
    val resultFuture: Future[String] = response match {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        entity.dataBytes.runFold(ByteString(""))(_ ++ _).map { body =>
          body.utf8String
        }
      case resp @ HttpResponse(code, _, _, _) => {
        outputError(config)("Request failed, response code: " + code)
        resp.discardEntityBytes()
        Future("")
      }
    }

    val result = Await.result(resultFuture, Duration.Inf)

    if(config.raw) {
      outputResult(config)(result)
    } else {
      val unmarshalledFuture = Unmarshal(result).to[List[SearchResult]]

      unmarshalledFuture.onComplete {
        case Failure(e) => {
          outputError(config)(result)
        }
        case _ =>
      }

      val unmarshalled = Await.result(unmarshalledFuture, Duration.Inf)
      outputInformation(config)(s"Found ${unmarshalled.size} item(s).")
      outputResult(config)(unmarshalled)
    }
  }

  case class Query(query : String, pretty : Option[Boolean] = Some(true))
}
