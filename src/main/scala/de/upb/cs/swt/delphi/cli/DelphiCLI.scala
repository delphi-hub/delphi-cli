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

import de.upb.cs.swt.delphi.cli.commands.{RetrieveCommand, TestCommand}


/**
  * The application class for the Delphi command line interface
  */
object DelphiCLI extends App {

  val cliParser = {
    new scopt.OptionParser[Config]("delphi-cli") {
      head("Delphi Command Line Tool", s"(${BuildInfo.version})")


      version("version").text("Prints the version of the command line tool.")

      help("help").text("Prints this help text.")
      override def showUsageOnError = true

      opt[String]("server").action( (x,c) => c.copy(server = x)).text("The url to the Delphi server")
      checkConfig(c => if (c.server.isEmpty()) failure("Option server is required.") else success)

      cmd("test").action((_,c) => c.copy(mode = "test"))

      cmd("retrieve").action((s,c) => c.copy(mode = "retrieve"))
        .text("Retrieve a project's description, specified by ID.")
        .children(
          arg[String]("ID").action((x, c) => c.copy(args = List(x))).text("The ID of the project to retrieve"),
          opt[Unit]('f', "file").action((_, c) => c.copy(opts = List("file"))).text("Use to load the ID from file, " +
            "with the filepath given in place of the ID")
        )

      //cmd("search")
    }
  }


  cliParser.parse(args, Config()) match {
    case Some(config) =>
      cliParser.showHeader()
      config.mode match {
        case "test" => TestCommand.execute(config)
        case "retrieve" => RetrieveCommand.execute(config)
        case _ => println("Unknown command")
      }

    case None => println("nope")
  }


}
