plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
    application
}

group = "com.mobilesync"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.mobilesync.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runSync") {
    group = "application"
    description = "Run sync engine against dataset"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.mobilesync.MainKt")
    workingDir = projectDir
    args = listOf(
        "--dataset", "../../dataset",
        "--output", "../outputs",
    )
}
