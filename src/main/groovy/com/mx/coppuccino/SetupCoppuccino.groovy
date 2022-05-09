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
