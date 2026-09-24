plugins {
    java
}

group = "me.uc_hussein"
version = "1.0.0"

description = "ULTRAS_TPA - A polished Paper TPA system by UC_Hussein"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.jar {
    archiveBaseName.set("ULTRAS_TPA")
    archiveVersion.set("1.0.0")
}
