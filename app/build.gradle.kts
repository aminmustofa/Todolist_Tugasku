plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Plugin yang diperlukan untuk Room (database)
    id("kotlin-kapt")
    // Plugin yang diperlukan untuk membuat data class 'Task' menjadi Parcelable
    id("kotlin-parcelize")
}

android {
    namespace = "com.min.todolist"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.min.todolist"
        minSdk = 21
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    // Aktifkan ViewBinding agar bisa mengakses layout XML dari kode Kotlin
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Dependensi dasar (dari file Anda)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    // Architectural Components - ViewModel, LiveData, dan Activity KTX
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.ktx)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Dukungan Coroutines untuk Room
    kapt(libs.androidx.room.compiler) // Annotation processor untuk Room

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Dependensi untuk testing (dari file Anda)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}
