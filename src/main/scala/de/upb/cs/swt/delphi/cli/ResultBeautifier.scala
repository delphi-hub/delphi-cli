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
import de.vandermeer.asciitable.{AsciiTable, CWC_LongestLine}
import de.vandermeer.asciithemes.{TA_Grid, TA_GridThemes}
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment

import scala.collection.JavaConverters._

object ResultBeautifier {
  def beautifySearchResults(results : Seq[SearchResult]) : String = {
    if (results.size == 0) {
      ""
    } else {
      val fieldNames = results.head.fieldNames()
      val tableHeader : Seq[String] = fieldNames.+:("Id")
      val tableBody: Seq[Seq[String]] = results.map {
        e => {
          Seq(e.toMavenIdentifier()).++(fieldNames.map(f => e.metricResults(f).toString))
        }
      }
        val table = tableBody.+:(tableHeader)

      val at = new AsciiTable()

      // add header
      val headerRow = at.addRow(table.head.asJavaCollection)
      asScalaBuffer(headerRow.getCells).tail.foreach { c => c.getContext.setTextAlignment(TextAlignment.RIGHT)}
      at.addRule()

      // add body
      table.tail.foreach { row: Iterable[String] => {
        val ar = at.addRow(row.asJavaCollection)
        asScalaBuffer(ar.getCells).tail.foreach { c => c.getContext.setTextAlignment(TextAlignment.RIGHT)}
      }}  

      at.getRenderer.setCWC(new CWC_LongestLine)
      at.setPaddingLeft(1)
      at.setPaddingRight(1)

      at.getContext.setFrameTopMargin(1)
      at.getContext.setFrameBottomMargin(1)
      at.getContext().setGridTheme(TA_GridThemes.INSIDE)


      at.render()
    }
  }

  def beautifyRetrieveResults(results : Seq[RetrieveResult]) : String = {
    results.map { r =>

      val basedata = {
        val at = new AsciiTable()

        at.addRule()

        at.addRow("source", r.metadata.source)
        at.addRow("artifactId", r.metadata.artifactId)
        at.addRow("groupId", r.metadata.groupId)
        at.addRow("version", r.metadata.version)
        at.addRow("discovered at", r.metadata.discovered)
        at.addRule()
        at.getRenderer.setCWC(new CWC_LongestLine)
        at.setPaddingLeftRight(1)
        at.getContext.setFrameLeftMargin(2)
        "↳ Basic information" + System.lineSeparator() +  at.render() + System.lineSeparator()
      }

      val metrics = r.metricResults.size match {
        case 0 => ""
        case _ => {
          val at = new AsciiTable()

          at.addRule()

          // add body
          for(
            (key, value) <- r.metricResults.toList.sortBy(x => x._1)
          ) {
            val ar = at.addRow(Seq(key, value).asJavaCollection)
            asScalaBuffer(ar.getCells).tail.foreach { c => c.getContext.setTextAlignment(TextAlignment.RIGHT)}
          }

          at.addRule()

          at.getRenderer.setCWC(new CWC_LongestLine)
          at.setPaddingLeftRight(1)
          at.getContext.setFrameLeftMargin(2)
          at.getContext.setFrameBottomMargin(1)
          "↳ Metrics:" + System.lineSeparator() + at.render()
        }
      }

      System.lineSeparator() + r.toMavenIdentifier() + System.lineSeparator()  + basedata + metrics + System.lineSeparator()

    }.fold("")(_ + _)
  }
  def beautifyFeatures(results : Seq[FieldDefinition]) : String = {

    if (results.size == 0) {
      ""
    } else {
      val tableHeader = Seq ("Name", "Description")
      val tableBody = results.sortBy(f => f.name).map(f => Seq(f.name, f.description))
      val table = tableBody.+:(tableHeader)

      val at = new AsciiTable()

      at.setTextAlignment(TextAlignment.JUSTIFIED_LEFT)
      at.getContext().setWidth(80)

      // add header
      at.addRule()
      at.addRow(table.head.asJavaCollection)
      at.addRule()

      // add body
      table.tail.foreach { row: Iterable[String] =>  at.addRow(row.asJavaCollection) }

      at.setPaddingLeft(1)
      at.setPaddingRight(1)
      at.getContext.setFrameTopMargin(1)
      at.getContext.setFrameBottomMargin(1)
      //at.getContext().setGridTheme(TA_GridThemes.)
      at.addRule()
      at.render()
    }
  }

}
