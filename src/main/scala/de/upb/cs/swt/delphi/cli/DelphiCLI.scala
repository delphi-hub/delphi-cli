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

package de.upb.cs.swt.delphi.cli

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import de.upb.cs.swt.delphi.cli.commands.{RetrieveCommand, SearchCommand, TestCommand}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}


/**
  * The application class for the Delphi command line interface
  */
object DelphiCLI extends App {

  implicit val system = ActorSystem()

  val cliParser = {
    new scopt.OptionParser[Config]("delphi-cli") {
      head("Delphi Command Line Tool", s"(${BuildInfo.version})")

      version("version").text("Prints the version of the command line tool.")

      help("help").text("Prints this help text.")
      override def showUsageOnError = true

      opt[String]("server").action( (x,c) => c.copy(server = x)).text("The url to the Delphi server")
      opt[Unit] (name = "raw").action((_,c) => c.copy(raw = true)).text("Output the raw results")
      opt[Unit] (name = "silent").action((_,c) => c.copy(silent = true)).text("Suppress non-result output")

      checkConfig(c => if (c.server.isEmpty()) failure("Option server is required.") else success)

      cmd("test").action((_,c) => c.copy(mode = "test"))

      cmd("retrieve").action((s,c) => c.copy(mode = "retrieve"))
        .text("Retrieve a project's description, specified by ID.")
        .children(
          arg[String]("id").action((x, c) => c.copy(id = x)).text("The ID of the project to retrieve"),
          opt[Unit]('f', "file").action((_, c) => c.copy(opts = List("file"))).text("Use to load the ID from file, " +
            "with the filepath given in place of the ID"),
          opt[String]("csv").action((x, c) => c.copy(csv = x)).text("Path to the output .csv file (overwrites existing file)")
        )

      cmd("search").action((s, c) => c.copy(mode = "search"))
        .text("Search artifact using a query.")
        .children(
          arg[String]("query").action((x,c) => c.copy(query = x)).text("The query to be used."),
          opt[Int]("limit").action((x, c) => c.copy(limit = Some(x))).text("The maximal number of results returned."),
          opt[Unit](name="list").action((_, c) => c.copy(list = true)).text("Output results as list (raw option overrides this)"),
          opt[String]("csv").action((x, c) => c.copy(csv = x)).text("Path to the output .csv file (overwrites existing file)")
        )
    }
  }


  cliParser.parse(args, Config()) match {
    case Some(config) =>
      if (!config.silent) cliParser.showHeader()
      config.mode match {
        case "test" => TestCommand.execute(config)
        case "retrieve" => RetrieveCommand.execute(config)
        case "search" => SearchCommand.execute(config)
        case x => config.consoleOutput.outputError(s"Unknown command: $x")
      }

    case None =>
  }


  val poolShutdown = Http().shutdownAllConnectionPools()
  Await.result(poolShutdown, Duration.Inf)

  implicit val ec: ExecutionContext = system.dispatcher
  val terminationFuture = system.terminate()

  terminationFuture.onComplete {
    sys.exit(0)
  }
}
