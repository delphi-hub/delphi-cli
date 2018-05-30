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
          arg[String]("ID").action((x, c) => c.copy(args = List(x))).text("The ID of the project to retrieve")
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
