cmake_minimum_required(VERSION 3.10.2)

project("kd_jni")
include_directories(../../../externals/WDL/WDL)
add_definitions(-DWDL_ALLOW_UNSIGNED_DEFAULT_CHAR -DWDL_SIMPLEPITCHSHIFT_IMPLEMENT)
add_definitions(-DWDL_RESAMPLE_TYPE=float)
add_library(
        wdl SHARED
        ../../../externals/WDL/WDL/fft.c
        ../../../externals/WDL/WDL/resample.cpp
        ../../../jni/wdl_fft.cpp
        ../../../jni/wdl_resample.cpp
        ../../../jni/wdl_simple_pitchshift.cpp
        ../../../jni/wdl_sinewavegen.cpp
)

target_link_libraries(wdl log)