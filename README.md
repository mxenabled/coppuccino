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
