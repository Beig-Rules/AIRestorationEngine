# GPU / NPU Backend Guide

## Current State
- `HardwareBackend` interface + `CpuBackend` / `GpuBackend` / `NpuBackend` scaffolds
- `BackendSelector` chooses based on `HardwarePreference`
- Pipeline already dispatches stages through the selected backend

## Enabling GPU (roadmap)

### Option A – ONNX Runtime (recommended)
1. Add `com.microsoft.onnxruntime:onnxruntime-android` + CUDA/DirectML EP where available
2. In `GpuBackend.initialize()`: create `OrtSession` with GPU providers
3. Export Real-ESRGAN / GFPGAN models to ONNX and place under `models/weights/`

### Option B – TensorFlow Lite GPU Delegate
1. Add TFLite + `tensorflow-lite-gpu`
2. Convert models to `.tflite`
3. Use `GpuDelegate` in `GpuBackend`

### Option C – Android NNAPI (NPU)
1. Probe `PackageManager` / `NnApiDelegate`
2. Set `NpuBackend.isAvailable = true` when a device accelerator is present

## Probe at startup
```kotlin
// In Application.onCreate()
EngineModule.probeHardware()
```

## Preference mapping
| User setting | Backend priority |
|--------------|------------------|
| AUTO         | NPU → GPU → CPU  |
| GPU          | GPU → CPU        |
| NPU          | NPU → GPU → CPU  |
| CPU          | CPU only         |
