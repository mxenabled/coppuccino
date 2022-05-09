# Coppuccino

![](https://s.yimg.com/aah/yhst-133668139843133/coppuccino-police-patch-17.jpg)

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
~/.m2/repository/com/mx/
```
