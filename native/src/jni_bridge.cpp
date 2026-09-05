#include <jni.h>
#include "tile_engine.h"
#include <stdexcept>

extern "C" {
JNIEXPORT jlong JNICALL Java_com_restoration_engine_android_NativeBridge_nativeCreateEngine(JNIEnv* env, jobject, jint tileSize, jint overlap, jfloat scale) {
    restoration::ProcessingConfig cfg;
    cfg.tileSize = tileSize; cfg.overlap = overlap; cfg.scale = scale;
    return reinterpret_cast<jlong>(new restoration::TileEngine(cfg));
}
JNIEXPORT void JNICALL Java_com_restoration_engine_android_NativeBridge_nativeDestroyEngine(JNIEnv*, jobject, jlong handle) {
    delete reinterpret_cast<restoration::TileEngine*>(handle);
}
JNIEXPORT void JNICALL Java_com_restoration_engine_android_NativeBridge_nativeCancel(JNIEnv*, jobject, jlong handle) {
    reinterpret_cast<restoration::TileEngine*>(handle)->requestCancel();
}
}
