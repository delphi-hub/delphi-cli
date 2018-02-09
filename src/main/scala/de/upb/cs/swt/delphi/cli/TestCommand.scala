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

    implicit val system = ActorSystem()
    implicit val executionContext = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(system))

    val uri = Uri(config.server)
    println(s"Contacting server ${uri}...")
    val resp = Http().singleRequest(HttpRequest(uri = uri.withPath(uri.path + "/version")))

    resp.onComplete {
      case Success(res) =>
        res.status match {
           case x if x.isSuccess() => {
             res.entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
               println("Successfully contacted Delphi server. ")
               println("Server version: " + body.utf8String)
             } }
             case _ => {
               println(s"Could not validate server ${config.server}. Error: ${res.status}.")
             }
           }
        system.terminate()
      case Failure(_) => {
        println(s"Could not reach server ${config.server}.")

        system.terminate()
      }
    }

  }
}
