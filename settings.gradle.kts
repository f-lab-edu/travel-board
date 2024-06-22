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
                "org.springframework.boot" -> useVersion(springBootVersion)
                "io.spring.dependency-management" -> useVersion(springDependencyManagementVersion)
            }
        }
    }
}