val maintainer: String = "KiyonoKara"
val packageName: String = "Scala-Events"

name := packageName
version := "0.0.1"
scalaVersion := "3.3.1"
crossScalaVersions := Seq("3.2.2", scalaVersion.value)
versionScheme := Some("semver-spec")

organization += "org.kiyo"
startYear := Some(2023)

homepage := Some(url(f"https://github.com/$maintainer/$packageName"))
licenses := Seq("Apache 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true
pomIncludeRepository := { _ => false }

publishTo := Some(f"GitHub $maintainer Apache Maven Packages" at f"https://maven.pkg.github.com/$maintainer/$packageName")
credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  maintainer,
  sys.env.getOrElse("GITHUB_TOKEN", new String())
)

lazy val root = (project in file("."))
  .settings(
    name := "Scala-Events"
  )
