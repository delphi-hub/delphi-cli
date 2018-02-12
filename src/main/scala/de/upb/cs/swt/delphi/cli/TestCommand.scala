package de.upb.cs.swt.delphi.cli

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString

import scala.util.{Failure, Success}

/**
  * The implementation of the test command.
  * Tries to connect to the Delphi server and reports on the results of the version call.
  */
object TestCommand extends Command {
  override def execute(config: Config): Unit = {

    val uri = Uri(config.server)
    println(s"Contacting server ${uri}...")
    val resp = BlockingHttpClient.doGet(uri.withPath(uri.path + "/version"))

    resp match {
      case Success(res) => {
        println("Successfully contacted Delphi server. ")
        println("Server version: " + res)
      }
      case Failure(_) => {
        println(s"Could not reach server ${config.server}.")
      }
    }

  }
}
