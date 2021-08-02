import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
    application
}

group = "com.tino1b2be"
version = "1.0.0-beta"

repositories {
    mavenCentral()
}

dependencies {
    implementation ("org.json:json:20210307")
    implementation(platform("io.vertx:vertx-stack-depchain:4.1.2"))
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-lang-kotlin")
    testImplementation("io.vertx:vertx-junit5")
    implementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set( "KotlobCLIKt" )
}

tasks.getByName<JavaExec>("run") {
    standardInput = System.`in`
}