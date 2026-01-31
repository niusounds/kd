plugins {
    id("com.android.kotlin.multiplatform.library")
    kotlin("multiplatform")
}

kotlin {
    android {
        namespace = "com.niusounds.kd"
        compileSdk = 36
    }
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        val jvmAndAndroid by creating {
            dependsOn(commonMain.get())
        }

        androidMain.get().dependsOn(jvmAndAndroid)
        jvmMain.get().dependsOn(jvmAndAndroid)

        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.network)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}
