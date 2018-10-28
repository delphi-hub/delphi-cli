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
