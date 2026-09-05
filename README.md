# 🚀 AI Restoration Engine (Elite Edition)

**Production-oriented, offline-first, memory-safe AI image restoration platform for Android.**

> Restore old, blurry, compressed or damaged photos using a multi-stage adaptive pipeline  
> (deblock → denoise → super-resolution → face restoration → post-processing)  
> with full control over quality, memory policy and hardware preference.

---

## ✨ Highlights

| Feature | Status |
|---------|--------|
| Pure Kotlin domain layer (zero Android deps) | ✅ |
| Rule-based adaptive pipeline planner | ✅ |
| Native C++ tile engine + AddressSanitizer | ✅ |
| Structured concurrency + cooperative cancellation | ✅ |
| Sealed `EngineError` with recovery hints | ✅ |
| Jetpack Compose multi-module UI | ✅ |
| Offline-only by design | ✅ |
| Unit tests (Kotlin + Python) | ✅ |
| Fuzz + Golden test skeletons | ✅ |
| Benchmarks | ✅ |
| CI (GitHub Actions) | ✅ |
| Privacy Policy + License inventory | ✅ |
| Release checklist + ProGuard rules | ✅ |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│  android/app + feature/{home,editor,result,settings}    │  Compose UI
├─────────────────────────────────────────────────────────┼
│  android/core/{ui, domain, engine-android}              │  Bridge + Theme
├─────────────────────────────────────────────────────────┼
│  engine/  (pure JVM)                                    │  Domain + Pipeline
├─────────────────────────────────────────────────────────┼
│  native/  (C++ / CMake / JNI)                           │  Tile engine + ASan
└─────────────────────────────────────────────────────────┘
```

See `docs/adr/` for Architecture Decision Records.

---

## ▶️ Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34
- CMake 3.22+ (for native)

### Open & Run
1. Clone the repository
2. Open the root folder in Android Studio
3. Let Gradle sync
4. Select the `android.app` run configuration
5. Run on an emulator or device (minSdk 24)

### Run tests
```bash
chmod +x scripts/*.sh
./scripts/run_tests.sh
# or
python3 tests/unit/test_planner.py
./gradlew :engine:test
```

### Build release
```bash
./scripts/build_release.sh
```

---

## 🧠 Pipeline Logic (simplified)

1. **ANALYZE** – quality profile (noise, blur, compression artifacts, faces)
2. **RuleBasedPipelinePlanner** decides stages:
   - High compression artifacts → `DEBLOCK`
   - High noise → `DENOISE`
   - Low resolution + scale > 1 → `SUPER_RESOLUTION`
   - Faces present + face restoration enabled → `FACE_RESTORATION`
   - Always ends with `POST_PROCESSING`
3. Progress is emitted as `RestoreProgress` (StageStarted / StageCompleted / Completed)
4. Cancellation is cooperative via `coroutineContext.isActive` + native `requestCancel()`

---

## 🛡️ Stability & Safety

- C++ uses `std::unique_ptr` exclusively for buffers
- AddressSanitizer enabled in Debug native builds
- `OutOfMemoryError` is caught and mapped to `EngineError.MemoryError`
- All failures are sealed errors with user message + diagnostic + recovery recommendation
- Fuzz tests for corrupt image handling

---

## 📦 Current State / Roadmap

| Phase | Content | Status |
|-------|---------|--------|
| 0–8 | Full skeleton (domain, native, UI, tests, docs, CI, release) | ✅ |
| 9 | Real ONNX / TFLite models + inference | 🕐 |
| 10 | Before/After slider + export | 🕐 |
| 11 | GPU / NPU backends | 🕐 |

The current codebase is a **solid, production-shaped skeleton**.  
All architecture, error handling, concurrency, UI wiring, tests and release tooling are in place.  
Real model inference is intentionally left as TODOs so the project remains lightweight and license-clean.

---

## 📜 License

MIT – see [LICENSE](LICENSE).  
Third-party model licenses are listed in [licenses/README.md](licenses/README.md).

---

## 🤝 Contributing

1. Keep the domain layer free of Android types
2. Prefer sealed classes for errors and progress
3. Add unit tests for any new planner rules
4. Run `./scripts/run_tests.sh` before opening a PR

---

**Ready for development.**  
After cloning → open in Android Studio → Sync → Run.
