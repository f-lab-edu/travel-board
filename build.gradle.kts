plugins {
    id("java-library")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management") apply false
}

val javaVersion: String by project
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

val projectGroup: String by project
val applicationVersion: String by project
allprojects {
    group = projectGroup
    version = applicationVersion

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    dependencies {
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.getByName("bootJar") {
        enabled = false
    }

    tasks.getByName("jar") {
        enabled = true
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    // setting for test separation
    val mainOutput = sourceSets.main.get().output
    val testOutput = sourceSets.test.get().output

    sourceSets {
        create("unitTest") {
            java.srcDir("src/test/unitTest/java")
            resources.srcDir("src/test/unitTest/resources")
            compileClasspath += mainOutput + testOutput
            runtimeClasspath += mainOutput + testOutput
        }
        create("e2eTest") {
            java.srcDir("src/test/e2eTest/java")
            resources.srcDir("src/test/e2eTest/resources")
            compileClasspath += mainOutput + testOutput
            runtimeClasspath += mainOutput + testOutput
        }
    }

    configurations {
        "unitTestImplementation" {
            extendsFrom(configurations.testImplementation.get())
        }
        "unitTestRuntimeOnly" {
            extendsFrom(configurations.testRuntimeOnly.get())
        }
        "e2eTestImplementation" {
            extendsFrom(configurations.testImplementation.get())
        }
        "e2eTestRuntimeOnly" {
            extendsFrom(configurations.testRuntimeOnly.get())
        }
    }

    tasks.register<Test>("unitTest") {
        description = "Runs unit tests."
        group = "verification"
        testClassesDirs = sourceSets["unitTest"].output.classesDirs
        classpath = sourceSets["unitTest"].runtimeClasspath
        useJUnitPlatform()
    }

    tasks.register<Test>("e2eTest") {
        description = "Runs E2E tests."
        group = "verification"
        testClassesDirs = sourceSets["e2eTest"].output.classesDirs
        classpath = sourceSets["e2eTest"].runtimeClasspath
        useJUnitPlatform()
    }

    tasks.named("test") {
        dependsOn("unitTest", "e2eTest")
    }
}
