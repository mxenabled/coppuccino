

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
