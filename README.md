# 🚀 AI Restoration Engine (Elite Edition)

**Offline-first, memory-safe AI image restoration for Android.**

Multi-stage pipeline: **deblock → denoise → super-resolution → face restoration → post-process**  
with adaptive planning, CPU/GPU/NPU backend selection, and interactive Before/After UI.

## Features

| Area | Status |
|------|--------|
| Pure Kotlin domain + sealed errors | ✅ |
| Rule-based pipeline planner | ✅ |
| Native C++ tile engine + ASan | ✅ |
| HardwareBackend CPU / GPU / NPU | ✅ |
| ONNX model catalog + Ort runner skeleton | ✅ |
| Jetpack Compose multi-module UI | ✅ |
| Before/After drag slider | ✅ |
| Save to Gallery (MediaStore) | ✅ |
| Share sheet | ✅ |
| RestoreSession (cross-screen state) | ✅ |
| Application probeHardware | ✅ |
| Tests / benchmarks / CI | ✅ |

## Quick start (Cursor)

```bash
git clone https://github.com/Beig-Rules/AIRestorationEngine.git
cd AIRestorationEngine
```

Edit freely in Cursor. For device run, open once in Android Studio → Sync → Run.

## Enable real ONNX inference

1. Put weights in `models/weights/`
2. Uncomment in `android/core/engine-android/build.gradle.kts`:
   `implementation("com.microsoft.onnxruntime:onnxruntime-android:1.17.1")`
3. Expand `OrtOnnxRunner` tensor bind (see source comments).

Docs: `docs/GPU_BACKEND.md`, `docs/SESSION_AND_ONNX.md`

## License

MIT — see [LICENSE](LICENSE).
