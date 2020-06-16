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

import org.joda.time.DateTime
import spray.json.{DefaultJsonProtocol, JsonFormat}
import de.upb.cs.swt.delphi.cli.artifacts.SearchResultJson.ResultJsonObject
import de.upb.cs.swt.delphi.cli.artifacts.SearchResultsJson.DateJsonFormat

trait StorageMetadata {
  val clientVersion: String
  val serverVersion: String
  val serverUrl: String
  val timestamp: DateTime
  val outputMode: String
}

case class QueryStorageMetadata(query: String,
                                results: Seq[Result],
                                resultLimit: Int,
                                clientVersion: String,
                                serverVersion: String,
                                serverUrl: String,
                                outputMode: String,
                                timestamp: DateTime) extends StorageMetadata

object StorageMetadataJson extends DefaultJsonProtocol {
  implicit val queryStorageMetadataFormat: JsonFormat[QueryStorageMetadata] = jsonFormat8(QueryStorageMetadata)
}
