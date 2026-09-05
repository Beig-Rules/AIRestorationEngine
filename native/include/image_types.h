#pragma once
#include <memory>
#include <cstdint>
#include <vector>
#include <atomic>

namespace restoration {
struct ImageBuffer {
    std::unique_ptr<uint8_t[]> data;
    size_t width = 0, height = 0, channels = 0;
    size_t byteSize() const { return width * height * channels; }
    bool isValid() const { return data && width > 0 && height > 0 && channels > 0; }
};

struct Tile {
    size_t x, y, w, h;
    size_t overlap;
};

struct ProcessingConfig {
    size_t tileSize = 256;
    size_t overlap = 32;
    float scale = 2.0f;
    bool cancelRequested = false;
};
}
