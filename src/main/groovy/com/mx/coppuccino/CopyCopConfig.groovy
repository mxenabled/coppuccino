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

import groovy.transform.CompileStatic
import java.nio.file.Paths

@CompileStatic
class CopyCopConfig {

  String projectRoot

  CopyCopConfig(String projectRoot) {
    this.projectRoot = projectRoot
  }

  void run() {
    ensureProjectDirectory(".coppuccino/")

    copyConfigFile("/com/mx/coppuccino/config/pmd/pmd.xml", ".coppuccino/pmd/pmd.xml")
    copyConfigFile("/com/mx/coppuccino/config/checkstyle/checkstyle.xml",".coppuccino/checkstyle/checkstyle.xml" )
    copyConfigFile("/com/mx/coppuccino/config/spotbugs/exclude.xml", ".coppuccino/spotbugs/exclude.xml")
    copyConfigFile("/com/mx/coppuccino/config/spotbugs/html-report-style.xsl", ".coppuccino/spotbugs/html-report-style.xsl")
    copyConfigFile("/com/mx/coppuccino/config/spotless/eclipse-formatter.xml", ".coppuccino/spotless/eclipse-formatter.xml")
    copyConfigFile("/com/mx/coppuccino/config/detekt/detekt.yml", ".coppuccino/detekt/detekt.yml")
  }

  private void copyConfigFile(String resourcePath, String dest) {
    File target = new File(Paths.get(projectRoot, dest).toString())
    ensureProjectDirectory(target.getParentFile().toPath().toString())
    InputStream stream = getClass().getResourceAsStream(resourcePath)
    target.text = ''
    target << stream.text
  }

  private void ensureProjectDirectory(String pathStr) {
    File path = new File(Paths.get(projectRoot, pathStr).toString())

    if (!path.exists()) {
      path.mkdirs()
    }
  }
}
