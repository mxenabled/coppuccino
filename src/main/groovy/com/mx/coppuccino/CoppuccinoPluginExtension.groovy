/**
 * Copyright 2020 MX Technologies.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mx.coppuccino

class CoppuccinoPluginExtension {
  String rootDir = ""
}

class CoppuccinoCoverageExtension {
  float minimumCoverage = 0.0
  String[] excludes = [
    'com.mx.mdx.models.*',
    'com.mx.mdx.Resources.*',
    'com.mx.mdx.Resources'
  ]
}


class CoppuccinoKotlinExtension {
  // Disable kotlin support by default
  boolean enabled = false
}
