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
        google() // WAJIB ADA untuk pustaka Google (CameraX, TFLite)
        mavenCentral() // WAJIB ADA untuk pustaka umum
    }
}

rootProject.name = "Looksy"
include(":app")
 