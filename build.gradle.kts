import org.jetbrains.intellij.tasks.RunIdeForUiTestTask

plugins {
    id("org.jetbrains.intellij") version "1.17.4"
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("java")
    id("io.qameta.allure") version "2.11.2"
}

intellij {
    version.set("2023.2")
    type.set("IU")
}

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
}

val remoteRobotVersion = "0.11.23"

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation("com.intellij.remoterobot:remote-robot:$remoteRobotVersion")
    testImplementation("com.intellij.remoterobot:remote-fixtures:$remoteRobotVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
    implementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.4")
    testImplementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    testImplementation("com.intellij.remoterobot:ide-launcher:$remoteRobotVersion")
    implementation("io.qameta.allure:allure-junit5:2.22.2")
}

tasks.named<RunIdeForUiTestTask>("runIdeForUiTests") {
    systemProperty("robot-server.port", "8082")
    systemProperty("ide.mac.message.dialogs.as.sheets", "false")
    systemProperty("jb.privacy.policy.text", "<!--999.999-->")
    systemProperty("jb.consents.confirmation.enabled", "false")
    systemProperty("ide.mac.file.chooser.native", "false")
    systemProperty("jbScreenMenuBar.enabled", "false")
    systemProperty("apple.laf.useScreenMenuBar", "false")
    systemProperty("idea.trust.all.projects", "true")
    systemProperty("ide.show.tips.on.startup.default.value", "false")
}

tasks.test {
    systemProperty("debug-retrofit", "enable")
    useJUnitPlatform()
}
