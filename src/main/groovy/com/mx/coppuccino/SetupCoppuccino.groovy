package com.mx.coppuccino

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths

@CompileStatic
class SetupCoppuccino extends DefaultTask {

  SetupCoppuccino() {
    group = 'build setup'
    description = 'Setup coppuccino plugin. Add coppuccino to .gitignore'
  }

  @TaskAction
  void run() {
    File target = new File(Paths.get(".gitignore").toString())
    target << "\n.coppuccino\n"
  }
}
