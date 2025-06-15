plugins {
    id("java")
    id("io.freefair.lombok") version "8.13.1"
}

group = "dev.amraleth"
version = "1.0-ALPHA"

repositories {
    mavenCentral()

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}