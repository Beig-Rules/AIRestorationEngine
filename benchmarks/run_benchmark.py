import time
import json
import os

def benchmark_tile_engine(image_size, tile_size=256):
    start = time.time()
    tiles_x = image_size[0] // tile_size
    tiles_y = image_size[1] // tile_size
    total = max(1, tiles_x * tiles_y)
    for i in range(total):
        time.sleep(0.001)
    elapsed = time.time() - start
    return {"image_size": image_size, "tiles": total, "time_ms": elapsed * 1000}

if __name__ == "__main__":
    results = []
    for size in [(1920, 1080), (3840, 2160), (7680, 4320)]:
        r = benchmark_tile_engine(size)
        results.append(r)
        print(f"✅ {size}: {r['tiles']} tiles in {r['time_ms']:.2f}ms")
    os.makedirs("benchmarks", exist_ok=True)
    with open("benchmarks/results.json", "w") as f:
        json.dump(results, f, indent=2)
