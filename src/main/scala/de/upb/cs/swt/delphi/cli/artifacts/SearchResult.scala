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

package de.upb.cs.swt.delphi.cli.artifacts

import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat}

trait Result{
  val id: String
  val metadata: ArtifactMetadata
  val metricResults: Map[String, Int]

  def toMavenIdentifier() : String = s"${metadata.groupId}:${metadata.artifactId}:${metadata.version}"

  def toMavenRelativeUrl(): String =
    s"${metadata.groupId.replace(".", "/")}/${metadata.artifactId}/${metadata.version}"

  def fieldNames() : List[String] = metricResults.keys.toList.sorted
}

case class SearchResult(id: String,
                        metadata: ArtifactMetadata,
                        metricResults: Map[String, Int]) extends Result

case class RetrieveResult(id: String,
                          metadata: ArtifactMetadata,
                          metricResults: Map[String, Int]) extends Result

case class ArtifactMetadata(val artifactId: String,
                            val source: String,
                            val groupId: String,
                            val version: String,
                            val discovered: String)

object SearchResultJson extends DefaultJsonProtocol {
  implicit val artifactFormat = jsonFormat5(ArtifactMetadata)
  implicit val searchResultFormat = jsonFormat3(SearchResult)
  implicit val retrieveResultFormat = jsonFormat3(RetrieveResult)

  implicit object ResultJsonObject extends RootJsonFormat[Result] {
    override def read(json: JsValue): Result = searchResultFormat.read(json)

    override def write(obj: Result): JsValue = obj match {
      case x: SearchResult => searchResultFormat.write(x)
      case x: RetrieveResult => retrieveResultFormat.write(x)
    }
  }
}