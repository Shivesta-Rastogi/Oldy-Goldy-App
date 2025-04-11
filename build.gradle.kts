plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}
android {
    namespace = "com.example.myfinalloginpage"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myfinalloginpage"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.firebase.firestore)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    //account page dependency
    implementation ("androidx.appcompat:appcompat:1.4.0")
    implementation ("androidx.activity:activity:1.6.0")

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage")
        implementation ("com.google.firebase:firebase-messaging:23.3.1")


    // Google Maps and Location
    implementation (libs.play.services.maps)
    implementation (libs.play.services.location)

    // Google Maps Directions API Client Library
    implementation (libs.google.maps.services)
    implementation (libs.slf4j.simple)


    implementation (libs.firebase.database.v2030)
    implementation (libs.firebase.auth)

    implementation ("com.google.firebase:firebase-firestore:24.7.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

}


