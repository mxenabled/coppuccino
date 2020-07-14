package com.mx.coppuccino

import com.diffplug.gradle.spotless.SpotlessPlugin
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test
import org.kordamp.gradle.plugin.jacoco.JacocoPlugin
import ru.vyarus.gradle.plugin.quality.QualityPlugin
import io.gitlab.arturbosch.detekt.DetektPlugin
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

@CompileStatic
class CoppuccinoPlugin implements Plugin<Project> {

  @CompileStatic(TypeCheckingMode.SKIP)
  void apply(Project project) {
    def extension = project.extensions.create('coppuccino', CoppuccinnoPluginExtension)

    project.plugins.withType(JavaPlugin) {
      project.afterEvaluate {
        def initCopConfig = new CopyCopConfig()
        initCopConfig.run()
      }

      project.afterEvaluate {
        project.tasks.register('configureCoppuccino', SetupCoppuccino)
        project.plugins.apply(QualityPlugin)
        project.plugins.apply(SpotlessPlugin)
        project.plugins.apply(DetektPlugin)
        project.plugins.apply(JacocoPlugin)
        project.configure(project) {

          // **************************************
          // Quality plugin configuration
          // **************************************
          quality {
            checkstyleVersion = '8.29'
            checkstyle = true
            pmd = true
            spotbugs = true
            configDir = '.coppuccino'
            sourceSets = [project.sourceSets.main]
            excludeSources = fileTree('build/generated')
          }

          // **************************************
          // Spotless plugin configuration
          // **************************************
          spotless {
            groovy {
              greclipse('2.3.0').configFile('.coppuccino/spotless/eclipse-formatter.xml')
              target project.fileTree(project.rootDir) {
                include '**/*.gradle', '**/*.groovy'
                exclude 'build/generated/**/*.*', '.gradle/**/*.*'
              }
            }

            java {
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

            kotlin {
              ktlint()
            }
          }

          // **************************************
          // Detekt plugin configuration
          // **************************************
          detekt {
            config = files(".coppuccino/detekt/detekt.yml")
            excludes: ".*build.*,.*/resources/.*,.*/tmp/.*"
          }

          // **************************************
          // JaCoCo test coverage configuration
          // **************************************

          test.finalizedBy jacocoTestReport
          check.dependsOn jacocoTestCoverageVerification

          jacocoTestReport {
            reports {
              csv.enabled true
              html.enabled true
            }
            executionData(test)
          }

          jacocoTestCoverageVerification {
            violationRules {
              rule {
                limit {
                  value = 'COVEREDRATIO'
                  minimum = extension.minimumCoverage
                }
                excludes = [
                    'com.mx.mdx.models.*',
                    'com.mx.mdx.Resources.*',
                    'com.mx.mdx.Resources'
                ]
              }
            }
          }

          // **************************************
          // JUnit test output
          //   Sends test success/failures out to
          //   console
          // **************************************
          tasks.withType(Test) {
            testLogging {
              // set options for log level LIFECYCLE
              events TestLogEvent.FAILED,
                  TestLogEvent.PASSED,
                  TestLogEvent.SKIPPED,
                  TestLogEvent.STANDARD_OUT
              exceptionFormat TestExceptionFormat.FULL
              showExceptions true
              showCauses true
              showStackTraces true

              // set options for log level DEBUG and INFO
              debug {
                events TestLogEvent.STARTED,
                    TestLogEvent.FAILED,
                    TestLogEvent.PASSED,
                    TestLogEvent.SKIPPED,
                    TestLogEvent.STANDARD_ERROR,
                    TestLogEvent.STANDARD_OUT
                exceptionFormat TestExceptionFormat.FULL
              }
              info.events = debug.events
              info.exceptionFormat = debug.exceptionFormat

              afterSuite { desc, result ->
                if (!desc.parent) { // will match the outermost suite
                  def output = "Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped)"
                  def startItem = '|  ', endItem = '  |'
                  def repeatLength = startItem.length() + output.length() + endItem.length()
                  println('\n' + ('-' * repeatLength) + '\n' + startItem + output + endItem + '\n' + ('-' * repeatLength))
                }
              }
            }
          }
        }
      }
    }
  }
}
