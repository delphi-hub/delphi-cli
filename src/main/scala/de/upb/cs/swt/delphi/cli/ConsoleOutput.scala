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
import de.upb.cs.swt.delphi.client.FieldDefinition

class ConsoleOutput(config: Config) {

  def outputInformation(value: String): Unit = {
    //noinspection ScalaStyle
    if (!config.silent) println(value)
  }

  def outputSuccess(value : String) : Unit = {
    //noinspection ScalaStyle
    if(!config.silent) println(fansi.Color.Green(value))
  }

  def outputResult(value: Any): Unit = {
    //noinspection ScalaStyle
    println(
      config.raw match {
        case true => value.toString
        case false => {
          value match {
            case Seq() => ""
            case searchResults : Seq[SearchResult] if searchResults.head.isInstanceOf[SearchResult]  => {
              config.list match {
                case true => searchResults.map(_.toMavenIdentifier()).mkString(System.lineSeparator())
                case false => ResultBeautifier.beautifySearchResults(searchResults)
              }
            }
            case retrieveResults : Seq[RetrieveResult] if retrieveResults.head.isInstanceOf[RetrieveResult]  => ResultBeautifier.beautifyRetrieveResults(retrieveResults)
            case featureResults : Seq[FieldDefinition] if featureResults.head.isInstanceOf[FieldDefinition] => ResultBeautifier.beautifyFeatures(featureResults)
            case _ => value.toString
          }
        }
      }
    )
  }

  def outputError(value : String) : Unit = {
    //noinspection ScalaStyle
    println(fansi.Color.Red(value))
  }
}
