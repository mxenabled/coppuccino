package com.mx.coppuccino

import com.diffplug.gradle.spotless.SpotlessPlugin
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
        project.plugins.apply(SpotlessPlugin)
        project.configure(project) {
          quality {
            checkstyleVersion = '8.29'
            checkstyle = true
            pmd = true
            spotbugs = true

            configDir = '.coppuccino'
            sourceSets = [project.sourceSets.main]
          }
          spotless {
            java {
              paddedCell()
              importOrder 'java', 'javax', 'edu', 'com', 'org', 'brave', 'io', 'reactor'
              // A sequence of package names
              eclipse().configFile '.coppuccino/spotless/eclipse-formatter.xml'
              // XML file dumped out by the Eclipse formatter
              removeUnusedImports()
              target project.fileTree(project.rootDir) {
                include '**/*.java'
                exclude 'build/generated/**/*.*', '.gradle/**/*.*'
              }
            }
          }
        }
      }
    }
  }
}
