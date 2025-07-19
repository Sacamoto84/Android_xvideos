plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.redgifs.common"
    compileSdk = 36

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    //implementation "androidx.core:core:1.16.0" // или последнюю доступную версию
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.material)
    implementation(libs.compose.theme.adapter)
    implementation(libs.androidx.material)

    implementation(libs.androidx.material3)

    implementation(libs.androidx.storage)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.volley)
    implementation(libs.ui.tooling)

    //implementation libs.firebase.auth
    //implementation libs.firebase.firestore.ktx

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Json
    implementation(libs.gson)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.okhttp)
    //implementation("io.ktor:ktor-client-jetty:2.2.3")
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.network)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    //Ебанное логирование
    //implementation(libs.ktor.client.logging)
    //implementation libs.slf4j.api
    //implementation libs.slf4j.simple

    // jsoup HTML parser library @ https://jsoup.org/
    implementation(libs.jsoup)
    implementation(libs.timber)
    implementation(libs.disk.lru.cache)

//    implementation(libs.coil.compose)
//    implementation(libs.coil.network.okhttp)
//    implementation(libs.coil.compose.core)
//    //implementation(libs.coil.network.ktor)
//    implementation(libs.coil.mp)

    implementation(libs.hilt.android)
    debugImplementation(libs.ui.tooling)
    ksp(libs.hilt.android.compiler)

    implementation(libs.voyager.navigator)
    implementation(libs.voyager.screenmodel)
    implementation(libs.voyager.bottom.sheet.navigator)
    implementation(libs.voyager.tab.navigator)
    implementation(libs.voyager.transitions)
    implementation(libs.voyager.hilt)
    implementation(libs.voyager.livedata)

    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    implementation(libs.androidx.material.icons.core)
    // Optional - Add full set of material icons
    implementation(libs.androidx.material.icons.extended)
    // Optional - Add window size utils
    implementation(libs.androidx.adaptive)

    ///////////////////////////////////////////////////////////////////////////
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.exoplayer.smoothstreaming)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.exoplayer.workmanager)
    implementation(libs.androidx.media3.common)
    // For loading data using the Cronet network stack
    //implementation "androidx.media3:media3-datasource-cronet:$media3_version"
    // For loading data using the OkHttp network stack
    //implementation "androidx.media3:media3-datasource-okhttp:$media3_version"
    // For loading data using librtmp
    //implementation "androidx.media3:media3-datasource-rtmp:$media3_version"
    implementation(libs.androidx.media3.ui.leanback)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.extractor)
    implementation(libs.androidx.media3.cast)
    implementation(libs.androidx.media3.transformer)
    //implementation "androidx.media3:media3-database:$media3_version"
    implementation(libs.androidx.media3.decoder)
    implementation(libs.androidx.media3.datasource)
    implementation(libs.androidx.media3.exoplayer.rtsp)
    implementation(libs.androidx.media3.ui.compose)
    implementation(libs.androidx.media3.effect)
    ///////////////////////////////////////////////////////////////////////////

    implementation(libs.kotlinx.serialization.json)

    //implementation "com.github.skydoves:landscapist-glide:2.4.1"
    implementation(libs.landscapist.placeholder)
    implementation(libs.landscapist.coil)

    //implementation('network.chaintech:compose-multiplatform-media-player:1.0.40')

    implementation(libs.components.resources)

    //implementation "io.sanghun:compose-video:1.2.0"

    //implementation 'com.firebaseui:firebase-ui-auth:7.2.0'
    // Required only if Facebook login support is required
    // Find the latest Facebook SDK releases here: https://goo.gl/Ce5L94
    //implementation 'com.facebook.android:facebook-android-sdk:8.x'

    //implementation platform(libs.firebase.bom)
    //implementation libs.firebase.analytics
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    //implementation(libs.google.firebase.auth)

    // Also add the dependency for the Google Play services library and specify its version
    //implementation(libs.play.services.auth)

    //implementation ("androidx.credentials:credentials")
    //implementation ("androidx.credentials:credentials-play-services-auth")
    //implementation ("com.google.android.libraries.identity.googleid:googleid")

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(libs.core)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

//    implementation("org.seleniumhq.selenium:selenium-java:4.15.0")
//    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.15.0")


//    // For AppWidgets support
//    implementation "androidx.glance:glance-appwidget:1.1.1"
//
//    // For Wear-Tiles support
//    implementation "androidx.glance:glance-wear-tiles:1.0.0-alpha05"

    implementation(libs.material3)

    implementation(libs.androidx.paging.runtime)   // core
    implementation(libs.androidx.paging.compose)   // compose

    //implementation("network.chaintech:sdp-ssp-compose-multiplatform:1.0.6")

    implementation(libs.zoomable)

    implementation(libs.androidx.lifecycle.process)
    implementation(libs.image.loader)

    implementation(libs.kdownloader)

    //https://github.com/amitshekhariitbhu/PRDownloader
    //implementation("com.github.amitshekhariitbhu:PRDownloader:1.0.2")

    implementation(libs.warden)

    implementation(libs.accompanist.systemuicontroller)

    //implementation("androidx.compose.runtime:runtime-tracing:1.8.1")
    //implementation("androidx.compose.runtime:runtime-tracing")


    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)

    implementation (project(":common"))
    implementation (project("::redgifs:db"))
    implementation (project("::redgifs:model"))
    implementation (project("::redgifs:network"))
}