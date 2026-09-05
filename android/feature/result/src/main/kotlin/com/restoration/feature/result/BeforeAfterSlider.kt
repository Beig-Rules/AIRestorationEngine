package com.restoration.feature.result

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlin.math.roundToInt

/**
 * Interactive Before/After comparison slider.
 * Drag the divider to reveal restored vs original image.
 */
@Composable
fun BeforeAfterSlider(
    beforeUri: String?,
    afterUri: String?,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableStateOf(0.5f) }
    var containerWidthPx by remember { mutableStateOf(1) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .onSizeChanged { containerWidthPx = it.width }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    val delta = dragAmount / containerWidthPx
                    sliderPosition = (sliderPosition + delta).coerceIn(0.02f, 0.98f)
                }
            }
    ) {
        if (afterUri != null) {
            AsyncImage(
                model = afterUri,
                contentDescription = "Restored image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            PlaceholderBox("After (restored)", MaterialTheme.colorScheme.primaryContainer)
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(sliderPosition)
                .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
        ) {
            if (beforeUri != null) {
                AsyncImage(
                    model = beforeUri,
                    contentDescription = "Original image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(with(density) { containerWidthPx.toDp() })
                )
            } else {
                PlaceholderBox("Before", MaterialTheme.colorScheme.secondaryContainer)
            }
        }

        val dividerX = (sliderPosition * containerWidthPx).roundToInt()
        Box(
            modifier = Modifier
                .offset { IntOffset(dividerX - 1, 0) }
                .width(2.dp)
                .fillMaxHeight()
                .background(Color.White)
        )
        Box(
            modifier = Modifier
                .offset { IntOffset(dividerX - with(density) { 14.dp.roundToPx() }, 0) }
                .align(Alignment.CenterStart)
                .size(28.dp)
                .background(Color.White, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("\u25C0\u25B6", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }

        Text(
            "BEFORE",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
        Text(
            "AFTER",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun PlaceholderBox(label: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
    }
}
