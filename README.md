[![Release](https://jitpack.io/v/mxenabled/coppuccino.svg)](https://jitpack.io/p/mxenabled/coppuccino)

# Coppuccino

Gradle plugin collection for Java/Kotlin/Groovy style, standard, and safety enforcement.

## What does it do?

Pulls together and configures the best java, kotlin, groovy style plugins. Defaults are setup for MX developers, but can be overridden.

Plugins:
* [Detekt](https://detekt.dev/)
* [Gradle Dependency Check](https://plugins.gradle.org/plugin/org.owasp.dependencycheck) (**Removed as of 2.0.0**)
* [Jacoco](https://www.jacoco.org/jacoco)
* [Spotless](https://github.com/diffplug/spotless/tree/main/plugin-gradle)
* [Gradle Quality Plugin](https://github.com/xvik/gradle-quality-plugin)
  * [Pmd](https://docs.gradle.org/current/userguide/pmd_plugin.html)
  * [Spotbugs](https://github.com/spotbugs/spotbugs-gradle-plugin)
  * [Checkstyle](https://docs.gradle.org/current/userguide/checkstyle_plugin.html)

## Installing

Coppuccino is hosted via [JitPack](https://jitpack.io/p/mxenabled/coppuccino). To import it into your project,
configure the JitPack repository in your `build.gradle`.

<!-- x-release-please-start-version -->
```groovy
plugins {
  id: "com.github.mxenabled.coppuccino" version "4.3.1"
}
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
<!-- x-release-please-end -->

## Configuration

Example configuration with default values:

_In build.gradle_
```
coppuccino {
  rootDir = "" # Relative path to project root
  overwriteConfigs = true # Set to false to allow a project to manage its own copy of configs (e.g. spotbugs/exclude.xml) 
  coverage {
    minimumCoverage = 0.0   # Required percentage of test code coverage.
    excludes [ # Package paths to exclude from coverage calculation
      'com.mx.mdx.models.*',
      'com.mx.mdx.Resources.*',
      'com.mx.mdx.Resources'
    ]
  }
  dependencies {
    lockingEnabled = true
    excludePreReleaseVersions = true # Set to false to allow for #.#.3.pre release versions to be included in --write-locks
  }
  java {
    enabled = true # Set to false to disable checkstyle (e.g. for projects that have no java files)
  }
  kotlin {
    enabled = false # Set to true to enable kotlin linting with Detekt
  }
}
```

Init MX style configurations

```
$ gradle initCopConfigs
```

## Deploying Locally

To create a local build of the accessor to use in connector services use

```shell
$ ./gradlew install
```

This will create a local build in your local maven repository that you can
then reference in other services.

On OXS using gradle the default location for the local maven repository is
```shell
~/.m2/repository/com/mx/
```
