# ADR-001: Engine Architecture & Dependency Flow

## Status
Accepted

## Context
The AI Restoration Engine requires strict separation of concerns for testability, platform independence, and memory safety.

## Decision
Layered, dependency-inverted architecture:

1. **UI Layer** (`android/app` + `feature/*`) – Jetpack Compose, depends only on domain interfaces
2. **Domain Layer** (`:engine`) – Pure Kotlin/JVM, zero Android dependencies
3. **Binding Layer** (`:android:core:engine-android`) – Bridges domain to native via JNI
4. **Native Layer** (`native/`) – C++ tile engine with AddressSanitizer in Debug builds

## Consequences
- Domain logic is 100% unit-testable without Android instrumentation
- Native crashes are isolated and translated to structured `EngineError`s
- Strict discipline required to prevent Android types from leaking into domain
