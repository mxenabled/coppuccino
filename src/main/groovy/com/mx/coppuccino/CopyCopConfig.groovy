package com.mx.coppuccino

import groovy.transform.CompileStatic
//import org.gradle.api.DefaultTask
//import org.gradle.api.tasks.Input
//import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths

@CompileStatic
class CopyCopConfig {

  CopyCopConfig() {
  }

  void run() {
    File target = new File(Paths.get(".coppuccino/pmd/pmd.xml").toString())
    target.getParentFile().mkdirs()
    InputStream stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/pmd/pmd.xml")
    target.text = ''
    target << stream.text

    target = new File(Paths.get(".coppuccino/checkstyle/checkstyle.xml").toString())
    target.getParentFile().mkdirs()
    stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/checkstyle/checkstyle.xml")
    target.text = ''
    target << stream.text

    target = new File(Paths.get(".coppuccino/spotbugs/exclude.xml").toString())
    target.getParentFile().mkdirs()
    stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/spotbugs/exclude.xml")
    target.text = ''
    target << stream.text

    target = new File(Paths.get(".coppuccino/spotless/eclipse-formatter.xml").toString())
    target.getParentFile().mkdirs()
    stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/spotless/eclipse-formatter.xml")
    target.text = ''
    target << stream.text

    target = new File(Paths.get(".coppuccino/detekt/detekt.yml").toString())
    target.getParentFile().mkdirs()
    stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/detekt/detekt.yml")
    target.text = ''
    target << stream.text
  }
}
