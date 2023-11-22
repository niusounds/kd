#include <jni.h>
#include "opus.h"

extern "C" {
JNIEXPORT jlong JNICALL
Java_com_niusounds_kd_opus_OpusEncoder_create(JNIEnv *env, jobject thiz,
                                              jint fs, jint channels, jint application) {
    int error;
    auto encoder = opus_encoder_create(fs, channels, application, &error);
    if (encoder) {
        return reinterpret_cast<jlong>(encoder);
    } else {
        return 0;
    }
}

JNIEXPORT void JNICALL
Java_com_niusounds_kd_opus_OpusEncoder_destroy(JNIEnv *env, jobject thiz,
                                               jlong ptr) {
    auto encoder = reinterpret_cast<OpusEncoder *>(ptr);
    opus_encoder_destroy(encoder);
}

JNIEXPORT jint JNICALL
Java_com_niusounds_kd_opus_OpusEncoder_encodeFloat(JNIEnv *env, jobject thiz,
                                                   jlong ptr,
                                                   jfloatArray pcmFloatArray,
                                                   jint frameSize,
                                                   jbyteArray dataByteArray,
                                                   jint maxDataBytes) {
    auto encoder = reinterpret_cast<OpusEncoder *>(ptr);
    auto pcm = env->GetFloatArrayElements(pcmFloatArray, 0);
    auto data = env->GetByteArrayElements(dataByteArray, 0);

    auto result = opus_encode_float(encoder, pcm, frameSize, (unsigned char *) data, maxDataBytes);

    env->ReleaseFloatArrayElements(pcmFloatArray, pcm, 0);
    env->ReleaseByteArrayElements(dataByteArray, data, 0);

    return result;
}

}
