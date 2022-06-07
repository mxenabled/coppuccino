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

import com.diffplug.gradle.spotless.SpotlessPlugin
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ComponentSelection
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test
import org.owasp.dependencycheck.gradle.DependencyCheckPlugin
import org.kordamp.gradle.plugin.jacoco.JacocoPlugin
import ru.vyarus.gradle.plugin.quality.QualityPlugin
import io.gitlab.arturbosch.detekt.DetektPlugin
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

@CompileStatic
class CoppuccinoPlugin implements Plugin<Project> {

  @CompileStatic(TypeCheckingMode.SKIP)
  void apply(Project project) {

    // Register extensions for config DSL
    def coppuccino = project.extensions.create('coppuccino', CoppuccinoPluginExtension)
    def coverage = project.extensions.coppuccino.extensions.create('coverage', CoppuccinoCoverageExtension)
    def dependencies = project.extensions.coppuccino.extensions.create('dependencies', CoppuccinoDependenciesExtension)
    def kotlinEx = project.extensions.coppuccino.extensions.create('kotlin', CoppuccinoKotlinExtension)

    project.plugins.withType(JavaPlugin) {
      project.afterEvaluate {
        def initCopConfig = new CopyCopConfig()
        initCopConfig.run(coppuccino.rootDir)
      }

      project.afterEvaluate {
        project.tasks.register('configureCoppuccino', SetupCoppuccino)
        project.plugins.apply(QualityPlugin)
        project.plugins.apply(SpotlessPlugin)
        if (kotlinEx.enabled) {
          project.plugins.apply(DetektPlugin)
        }
        project.plugins.apply(JacocoPlugin)
        project.plugins.apply(DependencyCheckPlugin)
        project.configure(project) {

          // **************************************
          // Dependency Check Scanning
          // **************************************
          dependencyCheck {
            data { directory=".dependency-check-data" }
            cveValidForHours=24
            failBuildOnCVSS=4
            format='HTML'
            skipConfigurations=[
                    'checkstyle',
                    'detekt',
                    'detektPlugins',
                    'pmd',
                    'spotbugs',
                    'spotbugsPlugins',
                    'spotbugsSlf4j'
            ]
            suppressionFile="${coppuccino.rootDir}dependency_suppression.xml"
          }

          // **************************************
          // Quality plugin configuration
          // **************************************
          quality {
            checkstyleVersion = '8.29'
            checkstyle = true
            codenarc = false
            pmd = true
            spotbugs = true
            configDir = "${coppuccino.rootDir}.coppuccino"
            sourceSets = [project.sourceSets.main]
            excludeSources = fileTree("${coppuccino.rootDir}build/generated")
            excludeSources = excludeSources.plus(fileTree("${coppuccino.rootDir}build/generatedsources"))
          }

          // **************************************
          // Spotless plugin configuration
          // Configuration options:
          //   https://github.com/diffplug/spotless/tree/master/plugin-gradle
          // **************************************
          spotless {
            groovy {
              importOrder('java', 'javax', 'edu', 'com', 'org', 'brave', 'io', 'reactor')
              greclipse('4.10.0').configFile("${coppuccino.rootDir}.coppuccino/spotless/eclipse-formatter.xml")
              target "**/*.gradle", "**/*.groovy"
              targetExclude "build/generated/**/*.*", ".gradle/**/*.*", "build/generatedsources/**/*.*"
            }

            java {
              importOrder ('java', 'javax', 'edu', 'com', 'org', 'brave', 'io', 'reactor')
              // A sequence of package names
              eclipse().configFile "${coppuccino.rootDir}.coppuccino/spotless/eclipse-formatter.xml"
              // XML file dumped out by the Eclipse formatter
              removeUnusedImports()
              target "**/*.java"
              targetExclude "build/generated/**/*.*", ".gradle/**/*.*", "build/generatedsources/**/*.*"
            }

            if (kotlinEx.enabled) {
              kotlin {
                ktlint('0.39.0').userData(
                    [
                        'indent_size'             : '2',
                        'continuation_indent_size': '2',
                        'kotlin_imports_layout'   : 'idea'
                    ]
                )
              }
            }
          }

          // **************************************
          // Detekt plugin configuration
          // **************************************
          if (kotlinEx.enabled) {
            detekt {
              config = files("${coppuccino.rootDir}.coppuccino/detekt/detekt.yml")
              excludes: "${coppuccino.rootDir}.*build.*,.*/resources/.*,.*/tmp/.*"
            }
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
            afterEvaluate {
              classDirectories.setFrom(classDirectories.files.collect {
                fileTree(dir: it, exclude: coverage.excludes)
              })
            }
            executionData(test)
          }

          jacocoTestCoverageVerification {
            violationRules {
              rule {
                limit {
                  value = 'COVEREDRATIO'
                  minimum = coverage.minimumCoverage
                }
                afterEvaluate {
                    classDirectories.setFrom(classDirectories.files.collect {
                        fileTree(dir: it, exclude: coverage.excludes)
                    })
                }
                excludes = coverage.excludes
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

          if (dependencies.lockingEnabled) {
            dependencyLocking { lockAllConfigurations() }
          }

          if (dependencies.excludePreReleaseVersions) {
            configurations.all {
              resolutionStrategy {
                cacheDynamicVersionsFor 0, "seconds"
                componentSelection {
                  // ignore all versions that end with 'pre'
                  all { ComponentSelection selection ->
                    if (selection.candidate.version.endsWith('pre')) {
                      selection.reject("pre versions are ignored")
                    }
                  }
                }
              }
            }
          }

          project.tasks.spotlessGroovy.dependsOn(compileJava, compileTestGroovy)
          project.tasks.spotlessJava.dependsOn(compileJava, compileTestGroovy, spotlessGroovy)

        }
      }
    }
  }
}
