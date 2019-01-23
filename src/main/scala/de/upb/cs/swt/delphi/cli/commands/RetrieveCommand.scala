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

import com.softwaremill.sttp.{Id, SttpBackend}
import de.upb.cs.swt.delphi.cli._
import de.upb.cs.swt.delphi.cli.artifacts.RetrieveResult
import spray.json._

import scala.io.Source

/**
  * The implementation of the retrieve command.
  * Retrieves the contents of the file at the endpoint specified by the config file, and prints them to stdout
  */
object RetrieveCommand extends Command with DefaultJsonProtocol {


  override def execute(implicit config: Config, backend: SttpBackend[Id, Nothing]): Unit = {

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

    println(config.id)
    val result = executeGet(
      Seq("retrieve", checkTarget),
      Map("pretty" -> "")
    )

    result.foreach(s => {
      if (config.raw) {
        reportResult.apply(s)
      }
      if (!config.raw || !config.csv.equals("")) {
        import artifacts.SearchResultJson._

        //TODO: Direct convertTo[List[RetrieveResult]] not working ???


        val jsonArr = s.parseJson.asInstanceOf[JsArray].elements
        val retrieveResults = jsonArr.map(r => r.convertTo[RetrieveResult])

        success.apply(s"Found ${retrieveResults.size} item(s).")
        reportResult.apply(retrieveResults)
        if (!config.csv.equals("")) {
          exportResult.apply(retrieveResults)
          information.apply("Results written to file '" + config.csv + "'")
        }
      }
    })
  }
}
