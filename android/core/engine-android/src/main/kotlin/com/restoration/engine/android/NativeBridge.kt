package com.restoration.engine.android
class NativeBridge {
    external fun nativeCreateEngine(tileSize: Int, overlap: Int, scale: Float): Long
    external fun nativeDestroyEngine(handle: Long)
    external fun nativeCancel(handle: Long)
    companion object { init { System.loadLibrary("restoration_native") } }
}
