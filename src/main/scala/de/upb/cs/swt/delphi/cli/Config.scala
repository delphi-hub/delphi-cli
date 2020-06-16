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

import de.upb.cs.swt.delphi.cli.OutputMode.OutputMode

/**
  * Represents a configuration for the Delphi CLI
  *
  * @param server  A server base uri (Defaults to env variable DELPHI_SERVER)
  * @param verbose Marker if logging should be verbose
  * @param mode    The command to be run
  */
case class Config(server: String = sys.env.getOrElse("DELPHI_SERVER", "https://delphi.cs.uni-paderborn.de/api"),
                  verbose: Boolean = false,
                  raw: Boolean = false,
                  csv: String = "",
                  output: String = "",
                  outputMode: Option[OutputMode] = None,
                  silent: Boolean = false,
                  list : Boolean = false,
                  mode: String = "",
                  query : String = "",
                  limit : Option[Int] = None,
                  id : String = "",
                  timeout : Option[Int] = None,
                  args: List[String] = List(),
                  opts: List[String] = List()) {

  lazy val consoleOutput = new ConsoleOutput(this)
  lazy val csvOutput = new CsvOutput(this)

}

object OutputMode extends Enumeration {
  type OutputMode = Value
  val JarOnly, PomOnly, All = Value

  def fromString(value:String): Option[OutputMode] = value.toLowerCase match {
    case "jaronly" => Some(JarOnly)
    case "pomonly" => Some(PomOnly)
    case "all" => Some(All)
    case _ => None
  }
}
