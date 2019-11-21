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

import com.softwaremill.sttp._
import de.upb.cs.swt.delphi.cli.Config

/**
  * Represents the implementation of a command of the CLI
  */
trait Command {

  /**
    * Executes the command implementation
    *
    * @param config The current configuration for the command
    */
  def execute(implicit config: Config, backend: SttpBackend[Id, Nothing]): Unit = {}


  /**
    * Http GET request template
    *
    * @param target     Sub url in delphi server
    * @param parameters Query params
    * @return GET response
    */
  protected def executeGet(paths: Seq[String], parameters: Map[String, String] = Map())
                          (implicit config: Config, backend: SttpBackend[Id, Nothing]): Option[String] = {
    val serverUrl = uri"${config.server}"
    val oldPath = serverUrl.path
    val reqUrl = serverUrl.path(oldPath ++ paths).params(parameters)
    val request = sttp.get(reqUrl)
    config.consoleOutput.outputInformation(s"Sending request ${request.uri}")
    val response = request.send()
    response.body match {
      case Left(value) =>
        error.apply(s"Request failed:\n $value")
        None
      case Right(value) =>
        Some(value)
    }
  }


  protected def information(implicit config: Config): String => Unit = config.consoleOutput.outputInformation _

  protected def reportResult(implicit config: Config): Any => Unit = config.consoleOutput.outputResult _

  protected def error(implicit config: Config): String => Unit = config.consoleOutput.outputError _

  protected def success(implicit config: Config): String => Unit = config.consoleOutput.outputSuccess _

  protected def exportResult(implicit config: Config): Any => Unit = config.csvOutput.exportResult _

}
