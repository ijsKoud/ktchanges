plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("com.varabyte.kotter:kotter-jvm:1.2.1")
    testImplementation("com.varabyte.kotterx:kotter-test-support-jvm:1.2.1")
    implementation("net.mamoe.yamlkt:yamlkt:0.13.0")
}

gradlePlugin {
    plugins {
        create("ktchanges") {
            id = "nl.klrnbk.daan.ktchanges"
            implementationClass = "nl.klrnbk.daan.ktchanges.KtchangesPlugin"
        }
    }
}

val functionalTestSourceSet =
    sourceSets.create("functionalTest") {}

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])
configurations["functionalTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    useJUnitPlatform()
}

gradlePlugin.testSourceSets.add(functionalTestSourceSet)

tasks.named<Task>("check") {
    dependsOn(functionalTest)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

kotlin { jvmToolchain(22) }
java { toolchain.languageVersion.set(JavaLanguageVersion.of(22)) }
