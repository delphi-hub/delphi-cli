package de.upb.cs.swt.delphi.cli.commands

import de.upb.cs.swt.delphi.cli.Config

/**
  * The implementation of the retrieve command.
  * Retrieves the contents of the file at the endpoint specified by the config file, and prints them to stdout
  */
object RetrieveCommand extends Command {
  override def execute(config: Config): Unit = executeGet(
    "/retrieve/" + config.args.head,
    s => println(s)
  )(config)
}
