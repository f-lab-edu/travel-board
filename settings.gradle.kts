rootProject.name = "travel-board"

include(
    "api-user",
    "api-admin",
    "support",
    "storage"
)

pluginManagement {
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.springframework.boot" -> useModule("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
                "io.spring.dependency-management" -> useModule("io.spring.gradle:dependency-management-plugin:$springDependencyManagementVersion")
            }
        }
    }
}