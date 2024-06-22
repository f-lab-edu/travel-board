tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

dependencies {
    implementation(project(":support"))
    implementation(project(":storage"))

    implementation("org.springframework.boot:spring-boot-starter-web")
}