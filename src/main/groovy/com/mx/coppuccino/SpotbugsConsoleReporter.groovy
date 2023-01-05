package com.mx.coppuccino

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Project
import org.gradle.internal.logging.text.StyledTextOutput.Style
import org.gradle.internal.logging.text.StyledTextOutputFactory

class SpotbugsConsoleReporter {
  static String toConsoleLink(File file) {
    return "file:///${noRootFilePath(file)}"
  }

  static String noRootFilePath(File file) {
    String path = file.canonicalPath.replaceAll('\\\\', '/')
    path.startsWith('/') ? path[1..-1] : path
  }

  static String unescapeHtml(String html) {
    new XmlSlurper().parseText("<t>${html.trim().replaceAll('\\&nbsp;', '')}</t>")
  }

  String NL = String.format('%n')

  private Project project
  private CoppuccinoPluginExtension coppuccino

  SpotbugsConsoleReporter(Project project, CoppuccinoPluginExtension coppuccino) {
    this.coppuccino = coppuccino
    this.project = project
  }

  @CompileStatic(TypeCheckingMode.SKIP)
  void report() {
    // report may not exists
    File reportFile = new File("build/reports/spotbugs/main.xml")
    if (reportFile == null || !reportFile.exists() || reportFile.length() == 0) {
      return
    }
    Node result = new XmlParser().parse(reportFile)
    int cnt = result.BugInstance.size()
    if (cnt > 0) {
      Node summary = result.FindBugsSummary[0]
      int fileCnt = summary.FileStats.findAll { (it.@bugCount as Integer) > 0 }.size()
      int p1 = summary.@priority_1 == null ? 0 : summary.@priority_1 as Integer
      int p2 = summary.@priority_2 == null ? 0 : summary.@priority_2 as Integer
      int p3 = summary.@priority_3 == null ? 0 : summary.@priority_3 as Integer

      def out = project.services.get(StyledTextOutputFactory).create("an-output")

      out.style(Style.FailureHeader).println("$NL$cnt ($p1 / $p2 / $p3) SpotBugs violations were found in ${fileCnt} " +
          "files$NL")

      Map<String, String> desc = buildDescription(result)
      Map<String, String> cat = buildCategories(result)
      Map<String, String> plugins = resolvePluginsChecks(project)
      result.BugInstance.each { bug ->
        Node msg = bug.LongMessage[0]
        Node src = bug.SourceLine[0]
        String bugType = bug.@type
        String description = unescapeHtml(desc[bugType])
        String srcPosition = src.@start
        String classname = src.@classname
        String pkg = classname[0..classname.lastIndexOf('.')]
        String cls = src.@sourcefile
        String plugin = plugins[bugType] ?: ''

        // part in braces recognized by intellij IDEA and shown as link
        out.style(Style.FailureHeader)
            .text("[${plugin}${cat[bug.@category]} | ${bugType}] $pkg(${cls}:${srcPosition})  ")
            .style(Style.Identifier).println("[priority ${bug.@priority} / rank ${bug.@rank}]")
            .style(Style.Failure).println("> Violation: ").style(Style.Error).println("  ${msg.text()}")
            .style(Style.Failure).println("> Details: ").style(Style.Error).println("  ${description}$NL")
      }

      generateHtmlReport(reportFile)
      String htmlReportUrl = toConsoleLink(project
          .file("${project.extensions.spotbugs.reportsDir.get()}/report.html"))

      out.style(Style.Info).println("${NL}SpotBugs HTML report: $htmlReportUrl")
    }
  }

  @CompileStatic(TypeCheckingMode.SKIP)
  void generateHtmlReport(File reportFile) {
    // html report
    String htmlReportPath = "${project.extensions.spotbugs.reportsDir.get()}/report.html"
    File htmlReportFile = project.file(htmlReportPath)
    // avoid redundant re-generation
    if (!htmlReportFile.exists() || reportFile.lastModified() > htmlReportFile.lastModified()) {
      project.ant.xslt(in: reportFile,
          style: new File("${coppuccino.rootDir}.coppuccino/spotbugs/html-report-style.xsl"),
          out: htmlReportPath,
      )
    }
  }

  @CompileStatic(TypeCheckingMode.SKIP)
  private Map<String, String> buildDescription(Node result) {
    Map<String, String> desc = [:]
    result.BugPattern.each { pattern ->
      desc[pattern.@type] = pattern.Details.text()
      //remove html tags
          .replaceAll('<(.|\n)*?>', '')
      // remove empty lines after tags remove (only one separator line remain)
          .replaceAll('([ \t]*\n){3,}', "$NL$NL")
      // reduce left indents
          .replaceAll('\n\t+', "$NL  ").replaceAll(' {2,}', '  ')
      // indent all not indented lines
          .replaceAll('\n([^\\s])', "$NL  \$1").trim()
    }
    return desc
  }

  @CompileStatic(TypeCheckingMode.SKIP)
  private Map<String, String> buildCategories(Node result) {
    Map<String, String> cat = [:]
    result.BugCategory.each { category ->
      cat[category.@category] = category.Description.text()
    }
    return cat
  }

  @CompileStatic(TypeCheckingMode.SKIP)
  @SuppressWarnings('CatchException')
  private Map<String, String> resolvePluginsChecks(Project project) {
    Map<String, String> res = [:]
    project.configurations.getByName('spotbugsPlugins').resolve().each { jar ->
      try {
        FileUtils.loadFileFromJar(jar, 'findbugs.xml') { InputStream desc ->
          Node result = new XmlParser().parse(desc)
          // to simplify reporting
          String provider = result.@provider + ' | '
          result.Detector.each {
            it.@reports.split(',').each { String name ->
              res.put(name, provider)
            }
          }
        }
      } catch (Exception ignore) {
        // it may be dependencies jars or format could suddenly change
      }
    }
    res
  }
}
