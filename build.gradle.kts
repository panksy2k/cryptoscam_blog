import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.crypto.scams"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.5.1"
val junitJupiterVersion = "5.9.1"

val mainVerticleName = "com.crypto.scams.cryptoscam.verticles.CryptoScamBlogProcess"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web-client")
  implementation("io.vertx:vertx-config")
  implementation("io.vertx:vertx-web")
  implementation("commons-net:commons-net:3.8.0")
  implementation("io.vertx:vertx-pg-client")
  implementation("com.google.inject:guice:5.0.1")
  implementation("am.ik.yavi:yavi:0.9.1")
  implementation("io.vertx:vertx-web-validation")
  implementation("io.vertx:vertx-web-templ-thymeleaf")
  implementation("io.vertx:vertx-shell")
  implementation("org.testcontainers:postgresql:1.16.2")
  implementation("org.testcontainers:testcontainers:1.16.2")

  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
