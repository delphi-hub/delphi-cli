package de.upb.cs.swt.delphi.cli.commands

import de.upb.cs.swt.delphi.cli.Config
import scala.io.Source

/**
  * The implementation of the retrieve command.
  * Retrieves the contents of the file at the endpoint specified by the config file, and prints them to stdout
  */
object RetrieveCommand extends Command {
  override def execute(config: Config): Unit = {
    //Checks whether the ID should be loaded from a file or not, and either returns the first line
    //  of the given file if it is, or the specified ID otherwise
    def checkTarget: String = {
      if (config.opts.contains("file")) {
        val source = Source.fromFile(config.args.head)
        val target = source.getLines.next()
        source.close()
        target
      } else config.args.head
    }
    executeGet(
      "/retrieve/" + checkTarget,
      s => println(s)
    )(config)
  }
}
