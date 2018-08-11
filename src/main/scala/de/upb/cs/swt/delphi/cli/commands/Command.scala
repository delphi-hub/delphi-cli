package de.upb.cs.swt.delphi.cli.commands

import akka.http.scaladsl.model.Uri
import de.upb.cs.swt.delphi.cli.{BlockingHttpClient, Config}

import scala.util.{Failure, Success}

/**
  * Represents the implementation of a command of the CLI
  */
trait Command {

  /**
    * Executes the command implementation
    * @param config The current configuration for the command
    */
  def execute(config: Config): Unit

  /**
    * Implements a common request type using currying to avoid code duplication
    * @param target The endpoint to perform a Get request on
    * @param onSuccess The function to perform on the response (eg. printing it)
    * @param config The current configuration for the command
    */
  protected def executeGet(target: String, onSuccess: String => Unit)(config: Config) : Unit = {

    val uri = Uri(config.server)
    println(s"Contacting server ${uri}...")
    val resp = BlockingHttpClient.doGet(uri.withPath(uri.path + target))

    resp match {
      case Success(res) => onSuccess(res)
      case Failure(_) => println(s"Could not reach server ${config.server}.")
    }

  }

}
