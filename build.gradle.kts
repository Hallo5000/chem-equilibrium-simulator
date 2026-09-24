plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "de.hallo5000"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<JavaExec>().configureEach {//add this to tests if using them
    jvmArgs("--enable-native-access=javafx.graphics")
}

application {
    mainClass.set("de.hallo5000.chemequilibriumsimulator.MainApplication")
}

javafx {
    version = "25.0.2"
    modules = listOf("javafx.controls", "javafx.fxml")
}
