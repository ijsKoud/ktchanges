plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.serialization") version "2.2.20"
}

version = providers.gradleProperty("version").get()

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.mockito:mockito-core:5.20.0")
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

tasks.named<Test>("test") {
    useJUnitPlatform()
}

kotlin { jvmToolchain(24) }
