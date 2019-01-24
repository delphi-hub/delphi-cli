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

import java.util.concurrent.TimeUnit

import com.softwaremill.sttp._
import com.softwaremill.sttp.sprayJson._
import de.upb.cs.swt.delphi.cli.artifacts.SearchResult
import de.upb.cs.swt.delphi.cli.{Config, artifacts}
import spray.json._

import scala.concurrent.duration._

object SearchCommand extends Command with DefaultJsonProtocol{

  val searchTimeout = 10.seconds
  val TIMEOUT_CODE = 408

  /**
    * Executes the command implementation
    *
    * @param config The current configuration for the command
    */
  override def execute(implicit config: Config, backend: SttpBackend[Id, Nothing]): Unit = {

    def query = config.query

    information.apply(s"Searching for artifacts matching ${'"'}$query${'"'}.")


    val queryParams = Map("pretty" -> "")
    val queryPayload: Query = Query(query,config.limit)
    val searchUri = uri"${config.server}/search?$queryParams"

    val request = sttp.body(queryPayload.toJson).post(searchUri)

    val (res, time) = processRequest(request)
    res.foreach(processResults(_, time))
  }

  private def processRequest(req: Request[String, Nothing])
                            (implicit config: Config,
                             backend: SttpBackend[Id, Nothing]): (Option[String], FiniteDuration) = {
    val start = System.nanoTime()
    val res: Id[Response[String]] = req.readTimeout(searchTimeout).send()
    val end = System.nanoTime()
    val took = (end - start).nanos

    if (res.code == TIMEOUT_CODE) {

      error.apply(s"The query timed out after   ${took.toSeconds} seconds. " +
        "To set a longer timeout, use the --timeout option.")
    }
    val resStr = res.body match {
      case Left(v) =>
        error.apply(s"Search request failed \n $v")
        println(v)
        None
      case Right(v) =>
        Some(v)
    }
    (resStr, took)
  }

  private def processResults(res: String, queryRuntime: FiniteDuration)(implicit config: Config) = {

    if (config.raw || res.equals("")) {
      reportResult.apply(res)
    }
    if (!(config.raw || res.equals("")) || !config.csv.equals("")) {
      import artifacts.SearchResultJson._
      val jsonArr = res.parseJson.asInstanceOf[JsArray].elements
      val retrieveResults = jsonArr.map(r => r.convertTo[SearchResult]).toList
      onProperSearchResults(retrieveResults)
    }

    def onProperSearchResults(sr: List[SearchResult]) = {

      val capMessage = {
        config.limit match {
          case Some(limit) if (limit <= sr.size)
          => s"Results may be capped by result limit set to $limit."
          case None if (sr.size >= 50)
          => "Results may be capped by default limit of 50 returned results. Use --limit to extend the result set."
          case _
          => ""
        }
      }

      success.apply(s"Found ${sr.size} item(s). $capMessage")
      reportResult.apply(sr)

      information.apply(f"Query took ${queryRuntime.toUnit(TimeUnit.SECONDS)}%.2fs.")

      if (!config.csv.equals("")) {
        exportResult.apply(sr)
        information.apply("Results written to file '" + config.csv + "'")
      }
    }
  }

}
