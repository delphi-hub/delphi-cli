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

import java.io.{BufferedWriter, FileWriter}

import de.upb.cs.swt.delphi.cli.artifacts.Result
import au.com.bytecode.opencsv.CSVWriter
import de.upb.cs.swt.delphi.cli.commands.SearchCommand.information
import de.vandermeer.asciitable.{AsciiTable, CWC_LongestLine}
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

/**
  * Export search and retrieve results to .csv file.
  *
  * @author Lisa Nguyen Quang Do
  * @author Ben Hermann
  *
  */

class CsvOutput(config: Config) {

  def exportResult(value: Any): Unit = {
    var table = value match {
      case results : Seq[Result] if results.head.isInstanceOf[Result] => resultsToCsv(results)
      case _ => {
        println("Error: results are in unknown format.")
        return
      }
    }

    val outputFile = new BufferedWriter(new FileWriter(config.csv, /* append = */false))
    val csvWriter = new CSVWriter(outputFile)
    csvWriter.writeAll(seqAsJavaList(table))
    outputFile.close()
    println("Results written to file '" + config.csv + "'")
  }

  def resultsToCsv(results : Seq[Result]) : Seq[Array[String]] = {
    if (results.size != 0) {
      val fieldNames = results.head.fieldNames()
      val tableHeader : Array[String] = (fieldNames.+:("discovered at").+:("version").+:("groupId").+:("artifactId").+:("source").+:("Id")).toArray
      val tableBody: Seq[Array[String]] = results.map {
        e => {
          Array(e.id, e.metadata.source, e.metadata.artifactId, e.metadata.groupId, e.metadata.version, e.metadata.discovered).++(fieldNames.map(f => e.metricResults(f).toString))
        }
      }
      return tableBody.+:(tableHeader)
    }
    return Seq.empty[Array[String]]
  }
}
