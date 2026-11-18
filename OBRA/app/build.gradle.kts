plugins { id("com.android.application") }
android {
    namespace = "com.obra.studio"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.obra.studio"
        minSdk = 26
        targetSdk = 37
        versionCode = 2
        versionName = "0.1.1-frontend"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
dependencies {
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("com.google.android.material:material:1.14.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:core:1.7.0")
}
