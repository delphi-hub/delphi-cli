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
package de.upb.cs.swt.delphi

import com.softwaremill.sttp.HttpURLConnectionBackend

package object cli {
  implicit val config: Config = Config()
  implicit val backend = HttpURLConnectionBackend()

  val javaLibPath = Option(System.getenv("JAVA_LIB_PATH"))
    .orElse(Some("/usr/lib/jvm/default-java/lib/"))

  val trustStorePath = Option(System.getenv("JAVA_TRUSTSTORE"))
    .orElse(Some("/usr/lib/jvm/default-java/lib/security/cacerts"))

}
