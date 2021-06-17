

# Coppuccino

![](https://s.yimg.com/aah/yhst-133668139843133/coppuccino-police-patch-17.jpg)

[Path Tools Issues](https://gitlab.mx.com/mx/money-experiences/path/path-issues/-/issues?scope=all&utf8=%E2%9C%93&state=opened&label_name[]=Path%20Tools)

Gradle plugin of Java style, standard, and safety enforcement.

Pulls together and configures:
* Spotless
* Gradle Quality Plugin
  * Pmd
  * Spotbugs
  * Checkstyle

```
plugins {
  id: "coppuccino"
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
~/.m2/repository/com/mx/path/service/accesssor/qolo/
```

## Deploying

* Merge Pull Request to Master
* Switch to `master` branch
* Update version in `build.gradle` (the version must be unique)
* Commit the updated `build.gradle`
  * `git add build.gradle&&git commit -m "Bump version to ?.?.?"`
* Push build.gradle update
  * `git push origin master`
* Release it `rake release`
