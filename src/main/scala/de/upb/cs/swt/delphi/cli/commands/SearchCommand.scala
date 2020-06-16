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
import de.upb.cs.swt.delphi.cli.{Config, FileOutput}
import de.upb.cs.swt.delphi.cli.artifacts.SearchResults
import de.upb.cs.swt.delphi.cli.artifacts.SearchResultsJson._
import spray.json._

import scala.concurrent.duration._

object SearchCommand extends Command with DefaultJsonProtocol{

  val searchTimeout = 10.seconds
  val timeoutCode = 408

  /**
    * Executes the command implementation
    *
    * @param config The current configuration for the command
    */
  override def execute(implicit config: Config, backend: SttpBackend[Id, Nothing]): Unit = {

    def query = config.query

    information.apply(s"Searching for artifacts matching ${'"'}$query${'"'}.")

    val queryPayload: Query = Query(query,config.limit)
    val searchUri = uri"${config.server}/search"

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

    if (res.code == timeoutCode) {

      error.apply(s"The query timed out after ${took.toSeconds}%.0f seconds. " +
        "To set a longer timeout, use the --timeout option.")
    }
    val resStr = res.body match {
      case Left(v) =>
        error.apply(s"Search request failed \n $v")
        None
      case Right(v) =>
        Some(v)
    }
    (resStr, took)
  }

  private def processResults(res: String, queryRuntime: FiniteDuration)
                            (implicit config: Config, backend: SttpBackend[Id, Nothing]) = {

    if (config.raw || res.equals("")) {
      reportResult.apply(res)
    }
    if (!(config.raw || res.equals("")) || !config.csv.equals("")) {
      val retrieveResults = res.parseJson.convertTo[SearchResults]
      val sr = retrieveResults.hits.toList
      val capMessage = {
        if (sr.size < retrieveResults.totalHits ) {
          config.limit match {
            case Some(limit) if (limit <= sr.size)
            => s"Results are capped by results limit set to $limit."
            case None if (sr.size >= 50)
            => "Results are capped by default limit of 50 returned results. Use --limit to extend the result set."
            case _
            => ""
          }
        } else {
          ""
        }
      }

      success.apply(s"Found ${retrieveResults.totalHits} item(s). $capMessage")
      reportResult.apply(sr)

      information.apply(f"Query roundtrip took ${queryRuntime.toUnit(TimeUnit.MILLISECONDS)}%.0fms.")

      if (!config.output.equals("")){
        val output = new FileOutput(executeGet(Seq("version")).getOrElse("UNKNOWN"))
        output.writeSearchResults(sr)
      }

      if (!config.csv.equals("")) {
        exportResult.apply(sr)
        information.apply("Results written to file '" + config.csv + "'")
      }
    }
  }




}
