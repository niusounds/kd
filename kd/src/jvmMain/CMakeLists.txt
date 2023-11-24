cmake_minimum_required(VERSION 3.27.8)

project(kd_jni)

find_package(JNI REQUIRED)
include_directories(${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH}/win32)

add_subdirectory(../androidMain build)
