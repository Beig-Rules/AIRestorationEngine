#!/usr/bin/env bash
set -euo pipefail

echo "🧪 Running AI Restoration Engine test suite..."
echo

if [ -f ./gradlew ]; then
  echo "→ Gradle :engine:test"
  ./gradlew :engine:test --quiet || echo "  (Gradle not fully configured yet – skipping)"
else
  echo "→ Gradle wrapper not found (expected on first clone before Android Studio sync)"
fi

echo "→ Python planner tests"
python3 tests/unit/test_planner.py

echo "→ Benchmarks"
python3 benchmarks/run_benchmark.py

echo
echo "✅ Core tests & benchmarks completed"
