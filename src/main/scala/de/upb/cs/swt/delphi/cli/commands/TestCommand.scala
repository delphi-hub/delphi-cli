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

import com.softwaremill.sttp.{Id, SttpBackend}
import de.upb.cs.swt.delphi.cli.Config

/**
  * The implementation of the test command.
  * Tries to connect to the Delphi server and reports on the results of the version call.
  */
object TestCommand extends Command {
  override def execute(implicit config: Config, backend: SttpBackend[Id, Nothing]): Unit = {
    executeGet(Seq("version"))
      .foreach(s => {
        success.apply("Successfully contacted Delphi server. ")
        information.apply("Server version: " + s)
      })
  }
}
