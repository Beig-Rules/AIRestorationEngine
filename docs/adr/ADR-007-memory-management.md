# ADR-007: Memory Management Under Maximum Pressure

## Status
Accepted

## Decision
1. **Kotlin**: Use `Flow` with explicit `coroutineContext.isActive` checks and structured concurrency
2. **C++**: Mandate `std::unique_ptr` for all image buffers (RAII)
3. **Build**: AddressSanitizer (ASan) enabled in Debug native builds
4. **Graceful Degradation**: Catch `OutOfMemoryError` and return structured `EngineError.MemoryError`

## Consequences
- Zero memory leaks guaranteed by RAII pattern
- Buffer overflows caught immediately in debug builds
- App never hard-crashes due to OOM; always degrades gracefully with a recoverable error
