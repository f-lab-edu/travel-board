tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

val bcprovVersion: String by project
dependencies {
    implementation(project(":support"))
    implementation(project(":storage"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.bouncycastle:bcprov-jdk18on:$bcprovVersion")

    testImplementation("org.springframework.security:spring-security-test")
}
