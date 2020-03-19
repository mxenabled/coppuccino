package com.mx.coppuccino

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import ru.vyarus.gradle.plugin.quality.QualityPlugin

@CompileStatic
class CoppuccinoPlugin implements Plugin<Project> {

  @CompileStatic(TypeCheckingMode.SKIP)
  void apply(Project project) {
    project.plugins.withType(JavaPlugin) {
      project.afterEvaluate {
        project.tasks.register('initCopConfig', InitCopConfig)

        project.plugins.apply(QualityPlugin)
        project.configure(project) {
          quality {
            checkstyleVersion = '8.29'
            checkstyle = true
            pmd = true
            spotbugs = true

            configDir = 'src/main/resources/coppuccino'
            sourceSets = [project.sourceSets.main]
          }
        }
      }
    }
  }
}
