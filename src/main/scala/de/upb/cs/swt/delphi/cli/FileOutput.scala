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

import java.io.{BufferedWriter, FileOutputStream, FileWriter}
import java.nio.file.{Files, Paths}

import com.softwaremill.sttp._
import de.upb.cs.swt.delphi.cli.artifacts.{QueryStorageMetadata, Result, RetrieveResult, SearchResult}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import spray.json._
import de.upb.cs.swt.delphi.cli.artifacts.StorageMetadataJson.queryStorageMetadataFormat

class FileOutput (serverVersion: String = "UNKNOWN")(implicit config:Config,  backend: SttpBackend[Id, Nothing]){

  def writeQueryResults(results: List[SearchResult]): Unit = {
    val metadata = buildQueryMetadata(results)

    val folderPath = Paths.get(config.output, DateTimeFormat.forPattern("YYYY-MM-dd_HH_mm_ss").print(metadata.timestamp))
    Files.createDirectory(folderPath)

    downloadResultFiles(results, metadata, folderPath.toString)
  }

  def writeRetrieveResults(results: Seq[RetrieveResult]): Unit = {
    val metadata = buildRetrieveMetadata(results)
    val first = results.head

    val timestamp = DateTimeFormat.forPattern("YYYY-MM-dd_HH_mm_ss").print(metadata.timestamp)
    val folderPath = Paths.get(config.output, s"${first.metadata.artifactId}-${first.metadata.version}-$timestamp")
    Files.createDirectory(folderPath)

    downloadResultFiles(results, metadata, folderPath.toString)
  }


  private def downloadResultFiles(results: Seq[Result], metadata: QueryStorageMetadata, folderPath: String) : Unit = {
    // Write Metadata first
    val metadataPath = Paths.get(folderPath, "query-metadata.json").toString
    val writer = new BufferedWriter(new FileWriter(metadataPath))
    writer.write(metadata.toJson.prettyPrint)
    writer.close()

    val outputMode = config.outputMode.getOrElse(OutputMode.PomOnly)

    info()
    outputMode match {
      case OutputMode.PomOnly => info(f"All associated POM files will be stored in $folderPath")
      case OutputMode.JarOnly => info(f"All associated JAR files will be stored in $folderPath")
      case _ => info(f"All associated JAR and POM files will be stored in $folderPath")
    }
    var progressCnt = 0f

    info()
    print("Downloading files: 00 %")

    results
      .map(r => r.toMavenRelativeUrl() + s"/${r.metadata.artifactId}-${r.metadata.version}")
      .map(relUrl =>  "https://repo1.maven.org/maven2/" + relUrl).foreach( urlWithoutExtension => {

      writeProgressValue((100f * progressCnt ).toInt / results.size)
      progressCnt += 1

      var artifactsToRetrieve = Seq[String]()
      if (outputMode == OutputMode.PomOnly || outputMode == OutputMode.All){
        artifactsToRetrieve = artifactsToRetrieve ++ Seq(s"$urlWithoutExtension.pom")
      }
      if(outputMode == OutputMode.JarOnly || outputMode == OutputMode.All){
        artifactsToRetrieve = artifactsToRetrieve ++ Seq(s"$urlWithoutExtension.jar")
      }
      artifactsToRetrieve.foreach( url => {
        sttp.get(uri"$url").response(asByteArray).send().body match {
          case Right(value) => new FileOutputStream(Paths.get(folderPath, url.splitAt(url.lastIndexOf('/'))._2).toString)
            .write(value)
          case Left(value) => error(f"Failed to download artifact from $url, got: $value")
        }
      })
    })
    writeProgressValue(100)
    info()
    info()
    info(f"Successfully wrote results to $folderPath.")
  }

  private def info(value: String = ""):Unit = config.consoleOutput.outputInformation(value)
  private def error(value: String = ""):Unit = config.consoleOutput.outputError(value)

  private def writeProgressValue(progressValue: Int): Unit = {
    print("\b\b\b\b")
    print(s"${if (progressValue < 10) f"0$progressValue" else progressValue} %")
  }
  private def buildQueryMetadata(results: List[SearchResult]) =
    QueryStorageMetadata( query = config.query,
      results = results,
      serverVersion = serverVersion,
      serverUrl = config.server,
      clientVersion = BuildInfo.version,
      timestamp = DateTime.now(),
      resultLimit = config.limit.getOrElse(50),
      outputMode = config.outputMode.getOrElse(OutputMode.PomOnly).toString
    )

  private def buildRetrieveMetadata(results: Seq[RetrieveResult]) =
    QueryStorageMetadata(query = f"Retrieve ${config.id}",
      results = results,
      serverVersion = serverVersion,
      serverUrl = config.server,
      clientVersion = BuildInfo.version,
      timestamp = DateTime.now(),
      resultLimit = 1,
      outputMode = config.outputMode.getOrElse(OutputMode.PomOnly).toString
    )
}
