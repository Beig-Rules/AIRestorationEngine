#pragma once
#include "image_types.h"
#include <functional>

namespace restoration {
class TileEngine {
public:
    TileEngine(const ProcessingConfig& config);
    ~TileEngine() = default;
    std::unique_ptr<ImageBuffer> processInTiles(const ImageBuffer& input, std::function<bool(size_t, size_t)> progressCb = nullptr);
    void requestCancel();
private:
    ProcessingConfig m_config;
    std::atomic<bool> m_cancelled{false};
    std::vector<Tile> generateTiles(size_t imgW, size_t imgH);
    std::unique_ptr<ImageBuffer> blendTiles(const std::vector<std::pair<Tile, std::unique_ptr<ImageBuffer>>>& tiles, size_t outW, size_t outH);
};
}
