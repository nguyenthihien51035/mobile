plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.btl_nhom1"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.btl_nhom1"
        minSdk = 34
        targetSdk = 36
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ViewPages - Slide
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.google.android.material:material:1.5.0")

    // Get, Set, Constructor
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    // AndroidX CartView
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")

    // Volley - Để call API
    implementation("com.android.volley:volley:1.2.1")

    // Gson - Để parse JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // OkHttp - Để call API
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Paging - Phân trang
    implementation("androidx.paging:paging-runtime:3.1.1")

    // Validation - Kiểm tra dữ liệu nhập vào
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.hibernate.validator:hibernate-validator:7.0.1.Final")

    // Glide - Load image từ URL
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}