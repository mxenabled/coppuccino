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

  CopyCopConfig() {
  }

  void run(String projectRoot) {
    File target = new File(Paths.get("${projectRoot}.coppuccino/pmd/pmd.xml").toString())
    target.getParentFile().mkdirs()
    InputStream stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/pmd/pmd.xml")
    target.text = ''
    target << stream.text

    target = new File(Paths.get("${projectRoot}.coppuccino/checkstyle/checkstyle.xml").toString())
    target.getParentFile().mkdirs()
    stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/checkstyle/checkstyle.xml")
    target.text = ''
    target << stream.text

    target = new File(Paths.get("${projectRoot}.coppuccino/spotbugs/exclude.xml").toString())
    target.getParentFile().mkdirs()
    stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/spotbugs/exclude.xml")
    target.text = ''
    target << stream.text

    target = new File(Paths.get("${projectRoot}.coppuccino/spotbugs/html-report-style.xsl").toString())
    target.getParentFile().mkdirs()
    stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/spotbugs/html-report-style.xsl")
    target.text = ''
    target << stream.text

    target = new File(Paths.get("${projectRoot}.coppuccino/spotless/eclipse-formatter.xml").toString())
    target.getParentFile().mkdirs()
    stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/spotless/eclipse-formatter.xml")
    target.text = ''
    target << stream.text

    target = new File(Paths.get("${projectRoot}.coppuccino/detekt/detekt.yml").toString())
    target.getParentFile().mkdirs()
    stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/detekt/detekt.yml")
    target.text = ''
    target << stream.text
  }
}
