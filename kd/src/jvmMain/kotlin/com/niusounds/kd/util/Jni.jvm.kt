package com.niusounds.kd.util

import com.niusounds.kd.Kd
import java.io.File

// this is not working...
actual fun loadNative(libname: String) {
    val libnameInResources = System.mapLibraryName(libname)

    val tmpFile = File.createTempFile("jni", null)
    tmpFile.deleteOnExit()

    Kd::class.java.getResourceAsStream("/$libnameInResources")?.use { src ->
        tmpFile.outputStream().use { dst ->
            src.copyTo(dst)
        }
    } ?: error("/$libnameInResources not found in resources")

    System.load(tmpFile.absolutePath)
}