package com.mx.coppuccino

class CoppuccinoPluginExtension {
}

class CoppuccinoCoverageExtension {
  float minimumCoverage = 0.0
  String[] excludes = [
    'com.mx.mdx.models.*',
    'com.mx.mdx.Resources.*',
    'com.mx.mdx.Resources'
  ]
}

class CoppuccinoDependenciesExtension {
  // Enable Dependency locking on all configurations
  boolean lockingEnabled = true

  // Ignore pre releases in dynamic version resolution
  boolean excludePreReleaseVersions = true
}

class CoppuccinoKotlinExtension {
  // Disable kotlin support by default
  boolean enabled = false
}
