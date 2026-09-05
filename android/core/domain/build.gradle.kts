plugins { id("com.android.library"); id("org.jetbrains.kotlin.android") }
android { namespace = "com.restoration.core.domain"; compileSdk = 34; defaultConfig { minSdk = 24 } }
dependencies { api(project(":engine")) }
