#include "tile_engine.h"
#include <stdexcept>
#include <cmath>
#include <algorithm>
#include <cstring>

namespace restoration {

TileEngine::TileEngine(const ProcessingConfig& config) : m_config(config) {}

std::vector<Tile> TileEngine::generateTiles(size_t imgW, size_t imgH) {
    std::vector<Tile> tiles;
    size_t step = m_config.tileSize - m_config.overlap;
    if (step == 0) step = 1;
    for (size_t y = 0; y < imgH; y += step) {
        for (size_t x = 0; x < imgW; x += step) {
            size_t tw = std::min(m_config.tileSize, imgW - x);
            size_t th = std::min(m_config.tileSize, imgH - y);
            tiles.push_back({x, y, tw, th, m_config.overlap});
        }
    }
    return tiles;
}

std::unique_ptr<ImageBuffer> TileEngine::processInTiles(const ImageBuffer& input, std::function<bool(size_t, size_t)> progressCb) {
    if (!input.isValid()) throw std::invalid_argument("Invalid input");
    size_t outW = static_cast<size_t>(input.width * m_config.scale);
    size_t outH = static_cast<size_t>(input.height * m_config.scale);
    auto output = std::make_unique<ImageBuffer>();
    output->width = outW; output->height = outH; output->channels = input.channels;
    output->data = std::make_unique<uint8_t[]>(output->byteSize());
    std::memset(output->data.get(), 0, output->byteSize());
    auto tiles = generateTiles(input.width, input.height);
    size_t processed = 0;
    for (const auto& tile : tiles) {
        if (m_cancelled.load()) throw std::runtime_error("Cancelled");
        // TODO: Run inference on tile, then blend
        processed++;
        if (progressCb && !progressCb(processed, tiles.size())) break;
    }
    return output;
}

void TileEngine::requestCancel() { m_cancelled.store(true); }

std::unique_ptr<ImageBuffer> TileEngine::blendTiles(const std::vector<std::pair<Tile, std::unique_ptr<ImageBuffer>>>& tiles, size_t outW, size_t outH) {
    auto result = std::make_unique<ImageBuffer>();
    result->width = outW; result->height = outH; result->channels = 3;
    result->data = std::make_unique<uint8_t[]>(result->byteSize());
    // TODO: Implement weighted blending with overlap regions
    return result;
}

}
