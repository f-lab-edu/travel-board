tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

val jjwtVersion: String by project
val restAssuredVersion: String by project
dependencies {
    implementation(project(":support"))
    implementation(project(":storage"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    e2eTestImplementation("org.testcontainers:junit-jupiter")
    e2eTestImplementation("org.springframework.boot:spring-boot-testcontainers")
    e2eTestImplementation("org.testcontainers:mysql")
    e2eTestImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("org.springframework.security:spring-security-test")
}
