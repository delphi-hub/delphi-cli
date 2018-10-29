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

import de.upb.cs.swt.delphi.cli.artifacts.{RetrieveResult, SearchResult}

class ConsoleOutput(config: Config) {

  def outputInformation(value: String): Unit = {
    if (!config.silent) println(value)
  }

  def outputResult(value: Any): Unit = {
    println(
      config.raw match {
        case true => value.toString
        case false => {
          value match {
            case Seq() => ""
            case searchResults : Seq[SearchResult] if searchResults.head.isInstanceOf[SearchResult]  => ResultBeautifier.beautifySearchResults(searchResults)
            case retrieveResults : Seq[RetrieveResult] if retrieveResults.head.isInstanceOf[RetrieveResult]  => ResultBeautifier.beautifyRetrieveResults(retrieveResults)
            case _ => value.toString
          }
        }
      }
    )
  }

  def outputError(value : String) : Unit = {
    println(value)
  }
}
