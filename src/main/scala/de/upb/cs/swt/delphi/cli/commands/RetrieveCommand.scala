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
      s"/retrieve/$checkTarget",
      s => println(s)
    )(config)
  }
}
