pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WeatherApp"

include(":app")
include(":core:core-common")
include(":core:core-network")
include(":core:core-domain")
include(":core:core-data")
include(":core:core-ui")
include(":feature:feature-today")
include(":feature:feature-weekly")
include(":feature:feature-city")
