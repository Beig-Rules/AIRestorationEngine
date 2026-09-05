# 🚀 AI Restoration Engine

**Offline-first, memory-safe AI photo restoration for Android.**

Pipeline: **ANALYZE → DEBLOCK → DENOISE → SUPER_RESOLUTION → FACE_RESTORATION → POST_PROCESSING**  
Adaptive planner · CPU/GPU/NPU backends · Compose UI · Before/After slider · Save & Share

## Status

| Capability | Done |
|------------|------|
| Domain models, sealed errors, planner, pipeline | ✅ |
| Native C++ tile engine + AddressSanitizer | ✅ |
| HardwareBackend (CPU / GPU / NPU) + selector | ✅ |
| ONNX ModelLoader + OrtOnnxRunner skeleton | ✅ |
| Compose: Home, Editor, Result, Settings | ✅ |
| Before/After slider | ✅ |
| Save to Gallery + Share + FileProvider | ✅ |
| RestoreSession cross-screen state | ✅ |
| Application probeHardware | ✅ |
| Structured concurrency + cancel | ✅ |
| Dispatchers.Default for process stages | ✅ |
| Release R8 minify | ✅ |
| Unit tests + Python checks + CI | ✅ |

## Cursor workflow

```bash
git clone https://github.com/Beig-Rules/AIRestorationEngine.git
cd AIRestorationEngine
```

Edit in Cursor. Device run: Android Studio once → Sync → Run (`android.app`).

```bash
python3 tests/unit/test_planner.py
```

## Real ONNX models

1. Weights in `models/weights/`
2. Uncomment `onnxruntime-android` in `engine-android/build.gradle.kts`
3. Expand tensor I/O in `OrtOnnxRunner`

Without models the app runs end-to-end with heuristic metrics.

## License

MIT — [LICENSE](LICENSE).
