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
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, Uri}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

/***
  * A blocking http client implemented using Akka HTTP
  */
object BlockingHttpClient {

  def doGet(uri : Uri) : Try[String] = {
    implicit val system = ActorSystem()
    implicit val executionContext = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(system))

    try {
      val req: Future[HttpResponse] = Http.get(system).singleRequest(HttpRequest(method = HttpMethods.GET, uri = uri))
      Await.result(req, Duration.Inf)

      val f = req.value.get.get.entity.dataBytes.runFold(ByteString(""))(_ ++ _)
      Await.result(f, Duration.Inf)

      Success(f.value.get.get.utf8String)
    } catch  {
      case e : Exception => Failure(e)
    } finally {
      system.terminate()
      Await.result(system.whenTerminated, Duration.Inf)
    }

  }

}

