#include "image_types.h"
#include <cmath>
#include <algorithm>

namespace restoration {
// Simple bilinear upscaling as baseline SR
void bilinearUpscale(const ImageBuffer& src, ImageBuffer& dst, float scale) {
    dst.width = static_cast<size_t>(src.width * scale);
    dst.height = static_cast<size_t>(src.height * scale);
    dst.channels = src.channels;
    dst.data = std::make_unique<uint8_t[]>(dst.byteSize());
    for (size_t y = 0; y < dst.height; y++) {
        for (size_t x = 0; x < dst.width; x++) {
            float srcX = x / scale, srcY = y / scale;
            size_t x0 = std::min(static_cast<size_t>(srcX), src.width - 1);
            size_t y0 = std::min(static_cast<size_t>(srcY), src.height - 1);
            size_t x1 = std::min(x0 + 1, src.width - 1);
            size_t y1 = std::min(y0 + 1, src.height - 1);
            float fx = srcX - x0, fy = srcY - y0;
            for (size_t c = 0; c < src.channels; c++) {
                float v00 = src.data[(y0 * src.width + x0) * src.channels + c];
                float v01 = src.data[(y0 * src.width + x1) * src.channels + c];
                float v10 = src.data[(y1 * src.width + x0) * src.channels + c];
                float v11 = src.data[(y1 * src.width + x1) * src.channels + c];
                float v = v00*(1-fx)*(1-fy) + v01*fx*(1-fy) + v10*(1-fx)*fy + v11*fx*fy;
                dst.data[(y * dst.width + x) * dst.channels + c] = static_cast<uint8_t>(std::clamp(v, 0.0f, 255.0f));
            }
        }
    }
}
}
