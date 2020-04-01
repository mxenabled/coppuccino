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
        def initCopConfig = new CopyCopConfig()
        initCopConfig.run()
      }

      project.afterEvaluate {
        project.tasks.register('configureCoppuccino', SetupCoppuccino)
        project.plugins.apply(QualityPlugin)
        project.configure(project) {
          quality {
            checkstyleVersion = '8.29'
            checkstyle = true
            pmd = true
            spotbugs = true

            configDir = '.coppuccino'
            sourceSets = [project.sourceSets.main]
          }
        }
      }
    }
  }
}
