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

import java.util.concurrent.TimeoutException
import java.util.concurrent.TimeUnit

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

    def query = config.query

    information(config)(s"Searching for artifacts matching ${'"'}$query${'"'}.")
    val start = System.nanoTime()

    implicit val queryFormat = jsonFormat2(Query)
    val baseUri = Uri(config.server)
    val prettyParam = Map("pretty" -> "")
    val searchUri = baseUri.withPath(baseUri.path + "/search").withQuery(akka.http.scaladsl.model.Uri.Query(prettyParam))
    val responseFuture = Marshal(Query(query, config.limit)).to[RequestEntity] flatMap { entity =>
      Http().singleRequest(HttpRequest(uri = searchUri, method = HttpMethods.POST, entity = entity))
    }

    try {
      val response = Await.result(responseFuture, Duration(config.timeout + " seconds"))
      val resultFuture: Future[String] = response match {
        case HttpResponse(StatusCodes.OK, headers, entity, _) =>
          entity.dataBytes.runFold(ByteString(""))(_ ++ _).map { body =>
            body.utf8String
          }
        case resp@HttpResponse(code, _, _, _) => {
          error(config)("Request failed, response code: " + code)
          resp.discardEntityBytes()
          Future("")
        }
      }

      val result = Await.result(resultFuture, Duration.Inf)

      val took = (System.nanoTime() - start).nanos.toUnit(TimeUnit.SECONDS)

      if (config.raw || result.equals("")) {
        reportResult(config)(result)
      }

      if(!(config.raw || result.equals("")) || !config.csv.equals("")) {
        val unmarshalledFuture = Unmarshal(result).to[List[SearchResult]]

        val processFuture = unmarshalledFuture.transform {
          case Success(unmarshalled) => {
            processResults(config, unmarshalled, took)
            Success(unmarshalled)
          }
          case Failure(e) => {
            error(config)(result)
            Failure(e)
          }
        }
      }
    } catch {
      case e : TimeoutException => {
        error(config)("The query timed out after " + (System.nanoTime() - start).nanos.toUnit(TimeUnit.SECONDS) +
          " seconds. To set a longer timeout, use the --timeout option.")
        Failure(e)
      }
    }
  }

  private def processResults(config: Config, results: List[SearchResult], queryRuntime: Double) = {
    val capMessage = {
      config.limit match {
        case Some(limit) if (limit <= results.size)
        => s"Results may be capped by result limit set to $limit."
        case None if (results.size >= 50)
        => "Results may be capped by default limit of 50 returned results. Use --limit to extend the result set."
        case _
        => ""
      }
    }
    success(config)(s"Found ${results.size} item(s). $capMessage")
    reportResult(config)(results)

    information(config)(f"Query took $queryRuntime%.2fs.")

    if(!config.csv.equals("")) {
      exportResult(config)(results)
      information(config)("Results written to file '" + config.csv + "'")
    }
  }

  case class Query(query: String,
                   limit: Option[Int] = None)

}
