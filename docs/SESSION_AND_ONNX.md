# Session state & ONNX integration

## RestoreSession
- Holds `originalUri` + `restoredUri` for the Before/After slider.
- `EditorViewModel` callbacks set from `AppNavigation`.
- Result screen reads `RestoreSession.state`.

## Application
- `RestorationApp` calls `EngineModule.probeHardware(context)` on startup.
- Manifest: `android:name=".RestorationApp"`.

## Model weights
```
models/weights/
  realesrgan_x4.onnx
  gfpgan_v1.4.onnx
  scunet.onnx
  deblock.onnx
```

## Enable ONNX Runtime
```kotlin
// android/core/engine-android/build.gradle.kts
implementation("com.microsoft.onnxruntime:onnxruntime-android:1.17.0")
```
Implement `OnnxSessionFactory` with OrtEnvironment / OrtSession, wire into CpuBackend/GpuBackend.runStage.

## Cursor workflow
1. Clone repo
2. Edit domain / Compose in Cursor
3. Open once in Android Studio for Gradle sync & device run
