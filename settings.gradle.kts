pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "AIRestorationEngine"
include(":android:app", ":android:feature:home", ":android:feature:editor",
        ":android:feature:result", ":android:feature:settings",
        ":android:core:ui", ":android:core:domain", ":android:core:engine-android")
include(":engine")
