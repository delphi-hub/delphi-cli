// Copyright (C) 2020 The Delphi Team.
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
import de.upb.cs.swt.delphi.client.FieldDefinition
import de.upb.cs.swt.delphi.client.FieldDefinitionJson._
import spray.json._

object FeaturesCommand extends Command {
  override def execute(implicit config: Config, backend: SttpBackend[Id, Nothing]): Unit = {
    val result = executeGet(Seq("features"))

    result.map(features => {
      if (config.raw) {
        reportResult.apply(features)
      }
      if (!config.raw || !config.csv.equals("")) {
        val featureList = features.parseJson.convertTo[JsArray].elements.map(e => e.convertTo[FieldDefinition])
        reportResult.apply(featureList)

        if (!config.csv.equals("")) {
          exportResult.apply(featureList)
          information.apply("Results written to file '" + config.csv + "'")
        }
      }
    })
  }
}
