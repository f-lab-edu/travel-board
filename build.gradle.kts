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
    sourceSets {
        val mainOutput = sourceSets.main.get().output
        val testOutput = sourceSets.test.get().output
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
        val testImplementationConfig = configurations.testImplementation.get()
        val testRuntimeOnlyConfig = configurations.testRuntimeOnly.get()
        "unitTestImplementation" {
            extendsFrom(testImplementationConfig)
        }
        "unitTestRuntimeOnly" {
            extendsFrom(testRuntimeOnlyConfig)
        }
        "e2eTestImplementation" {
            extendsFrom(testImplementationConfig)
        }
        "e2eTestRuntimeOnly" {
            extendsFrom(testRuntimeOnlyConfig)
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

    dependencies {
        add("e2eTestImplementation", "org.testcontainers:junit-jupiter")
        add("e2eTestImplementation", "org.springframework.boot:spring-boot-testcontainers")
        add("e2eTestImplementation", "org.testcontainers:mysql")
    }

    tasks.named("test") {
        dependsOn("unitTest", "e2eTest")
    }
}
