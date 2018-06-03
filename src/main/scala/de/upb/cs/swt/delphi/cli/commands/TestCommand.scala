package de.upb.cs.swt.delphi.cli.commands

import de.upb.cs.swt.delphi.cli.Config

/**
  * The implementation of the test command.
  * Tries to connect to the Delphi server and reports on the results of the version call.
  */
object TestCommand extends Command {
  override def execute(config: Config): Unit = executeGet(
    "/version",
    s => {
      println("Successfully contacted Delphi server. ")
      println("Server version: " + s)
    }
  )(config)
}
