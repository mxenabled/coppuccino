package com.mx.coppuccino

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths

@CompileStatic
class InitCopConfig extends DefaultTask {

  @Input
  boolean override = true

  InitCopConfig() {
    group = 'build setup'
    description = 'Copies default coppuccino plugin configuration files for customization'
  }

  @TaskAction
  void run() {
    File target = new File(Paths.get("src/main/resources/coppuccino/pmd/pmd.xml").toString())
    target.getParentFile().mkdirs()
    InputStream stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/pmd/pmd.xml")
    target.text = ''
    target << stream.text

    target = new File(Paths.get("src/main/resources/coppuccino/checkstyle/checkstyle.xml").toString())
    target.getParentFile().mkdirs()
    stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/checkstyle/checkstyle.xml")
    target.text = ''
    target << stream.text

    target = new File(Paths.get("src/main/resources/coppuccino/spotbugs/exclude.xml").toString())
    target.getParentFile().mkdirs()
    stream = getClass().getResourceAsStream("/com/mx/coppuccino/config/spotbugs/exclude.xml")
    target.text = ''
    target << stream.text
  }
}
