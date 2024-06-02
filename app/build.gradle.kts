import java.util.Properties

plugins {
    kotlin("kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.currencyconversion"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.currencyconversion"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "com.example.currencyconversion.viewModels.CustomTestRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "API_KEY", properties.getProperty("API_KEY"))
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}



dependencies {

    implementation ("androidx.multidex:multidex:2.0.1@aar")
    implementation("androidx.annotation:annotation:1.8.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.test:core-ktx:1.5.0")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    val hilt_version = "2.49"
    val hilt_work = "1.2.0"
    val lifecycle_version = "2.8.1"
    val retrofit_version = "2.9.0"
    val okhttp_version = "4.12.0"
    val gson_version = "2.10.1"
    val coroutine_version = "1.7.3"
    val roomVersion = "2.6.1"

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("androidx.test:rules:1.5.0")

    testImplementation("androidx.room:room-testing:$roomVersion")
    androidTestImplementation("androidx.room:room-testing:$roomVersion")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")

    // AndroidX Test - Core & Rules
    androidTestImplementation ("androidx.test:core:1.5.0")
    androidTestImplementation ("androidx.test:runner:1.5.2")
    androidTestImplementation ("androidx.test:rules:1.5.0")

    // AndroidX Test - Ext
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")

    // Hilt
    implementation("com.google.dagger:hilt-android:$hilt_version")
    kaptAndroidTest ("com.google.dagger:hilt-android-compiler:2.49")
    implementation("androidx.hilt:hilt-work:$hilt_work")
    kapt("androidx.hilt:hilt-compiler:$hilt_work")
    kapt("com.google.dagger:hilt-compiler:$hilt_version")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    androidTestImplementation ("com.google.dagger:hilt-android-testing:2.40.5@aar")




    //Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.activity:activity-ktx:1.9.0")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp_version")
    implementation("com.google.code.gson:gson:$gson_version")


    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutine_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutine_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutine_version")

    // Coroutines test library
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    //ROOM
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")


    // Mockito for mocking
    testImplementation ("org.mockito:mockito-core:4.0.0")
    testImplementation ("org.mockito:mockito-inline:4.0.0")
    androidTestImplementation ("org.mockito:mockito-android:4.0.0")

    // Mockito Kotlin
    testImplementation ("org.mockito.kotlin:mockito-kotlin:4.0.0")
}