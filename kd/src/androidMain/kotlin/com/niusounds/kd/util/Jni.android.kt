package com.niusounds.kd.util

actual fun loadNative(libname: String) {
    System.loadLibrary(libname)
}