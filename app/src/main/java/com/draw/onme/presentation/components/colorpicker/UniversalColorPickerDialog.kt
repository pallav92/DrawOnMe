package com.draw.onme.presentation.components.colorpicker

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.draw.onme.domain.model.StrokeColor
import kotlin.math.roundToInt

/**
 * Universal, child-friendly color picker dialog.
 * Suitable for coloring pages, free scribbling, and guided stencil sketching.
 *
 * Features:
 * - Live color preview capsule.
 * - Categorized color palettes (Rainbow, Pastels, Nature, Neon).
 * - Continuous rainbow hue slider for selecting any custom shade.
 * - Value / brightness adjustment slider.
 * - High-contrast selection indicators.
 */
@Composable
fun UniversalColorPickerDialog(
    initialColor: StrokeColor,
    onColorSelected: (StrokeColor) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Current HSV state
    val initialHsv = remember(initialColor) {
        ColorPickerPalette.strokeColorToHsv(initialColor)
    }

    var hue by remember { mutableFloatStateOf(initialHsv.first) }
    var saturation by remember { mutableFloatStateOf(initialHsv.second.coerceAtLeast(0.1f)) }
    var value by remember { mutableFloatStateOf(initialHsv.third.coerceAtLeast(0.1f)) }
    var selectedCategory by remember { mutableStateOf(ColorPickerPalette.AllCategories.first()) }

    var currentColor by remember {
        mutableStateOf(initialColor)
    }

    fun updateColorFromHsv(newHue: Float, newSat: Float, newVal: Float) {
        hue = newHue
        saturation = newSat
        value = newVal
        currentColor = ColorPickerPalette.hsvToStrokeColor(newHue, newSat, newVal)
    }

    fun selectPresetColor(strokeColor: StrokeColor) {
        currentColor = strokeColor
        val (h, s, v) = ColorPickerPalette.strokeColorToHsv(strokeColor)
        hue = h
        saturation = s
        value = v
    }

    val animatedColor by animateColorAsState(
        targetValue = Color(currentColor.argb),
        label = "PreviewColorAnimation"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 440.dp)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header: Title & Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pick a Color 🎨",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Live Color Preview Capsule
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(animatedColor)
                                .border(2.dp, Color.White, CircleShape)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Current Color",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val hexString = String.format("#%06X", (currentColor.argb and 0x00FFFFFFL))
                            Text(
                                text = hexString,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Compare with initial color if changed
                        if (currentColor != initialColor) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "Original",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color(initialColor.argb))
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                        .clickable { selectPresetColor(initialColor) }
                                )
                            }
                        }
                    }
                }

                // Continuous Rainbow Hue Slider
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Rainbow Spectrum",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    RainbowHueBar(
                        hue = hue,
                        onHueChanged = { newHue ->
                            updateColorFromHsv(
                                newHue = newHue,
                                newSat = if (saturation < 0.2f) 0.85f else saturation,
                                newVal = if (value < 0.2f) 0.9f else value
                            )
                        }
                    )
                }

                // Brightness / Shade Slider
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Brightness & Shade",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ShadeValueBar(
                        hue = hue,
                        value = value,
                        onValueChanged = { newVal ->
                            updateColorFromHsv(newHue = hue, newSat = saturation, newVal = newVal)
                        }
                    )
                }

                // Category Chips Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (category in ColorPickerPalette.AllCategories) {
                        val isSelected = category.id == selectedCategory.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text("${category.icon} ${category.title}")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                // Swatches Grid
                PaletteSwatchesGrid(
                    colors = selectedCategory.colors,
                    selectedColor = currentColor,
                    onSelectColor = { color -> selectPresetColor(color) }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Footer Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onColorSelected(currentColor)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Select Color",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Continuous rainbow horizontal slider bar allowing picking any Hue (0..360).
 */
@Composable
private fun RainbowHueBar(
    hue: Float,
    onHueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val rainbowColors = listOf(
        Color.Red,
        Color.Yellow,
        Color.Green,
        Color.Cyan,
        Color.Blue,
        Color.Magenta,
        Color.Red
    )
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(rainbowColors))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                    onHueChanged(fraction * 360f)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                    onHueChanged(fraction * 360f)
                }
            }
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val thumbRadiusPx = with(density) { 14.dp.toPx() }
        val thumbXPx = ((hue / 360f).coerceIn(0f, 1f) * widthPx - thumbRadiusPx)
            .coerceIn(0f, (widthPx - thumbRadiusPx * 2f).coerceAtLeast(0f))

        // Indicator thumb
        Box(
            modifier = Modifier
                .offset { IntOffset(thumbXPx.roundToInt(), 0) }
                .size(28.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(3.dp, Color.Black.copy(alpha = 0.7f), CircleShape)
        )
    }
}

/**
 * Shade and brightness bar for the active Hue.
 */
@Composable
private fun ShadeValueBar(
    hue: Float,
    value: Float,
    onValueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val pureHueColor = remember(hue) {
        val stroke = ColorPickerPalette.hsvToStrokeColor(hue, 1f, 1f)
        Color(stroke.argb)
    }

    val shadeGradient = Brush.horizontalGradient(
        listOf(Color.Black, pureHueColor, Color.White)
    )

    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(shadeGradient)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                    onValueChanged(fraction)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                    onValueChanged(fraction)
                }
            }
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val thumbRadiusPx = with(density) { 14.dp.toPx() }
        val thumbXPx = (value.coerceIn(0f, 1f) * widthPx - thumbRadiusPx)
            .coerceIn(0f, (widthPx - thumbRadiusPx * 2f).coerceAtLeast(0f))

        // Indicator thumb
        Box(
            modifier = Modifier
                .offset { IntOffset(thumbXPx.roundToInt(), 0) }
                .size(28.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(3.dp, Color.Black.copy(alpha = 0.7f), CircleShape)
        )
    }
}

/**
 * Flow row of child-friendly color swatches.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PaletteSwatchesGrid(
    colors: List<StrokeColor>,
    selectedColor: StrokeColor,
    onSelectColor: (StrokeColor) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (color in colors) {
            val isSelected = color.argb == selectedColor.argb
            val composeColor = Color(color.argb)

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .shadow(if (isSelected) 4.dp else 1.dp, CircleShape)
                    .clip(CircleShape)
                    .background(composeColor)
                    .border(
                        width = if (isSelected) 3.5.dp else 1.5.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.8f),
                        shape = CircleShape
                    )
                    .clickable(
                        onClick = { onSelectColor(color) },
                        role = Role.Button
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    val luminance = (0.299f * composeColor.red + 0.587f * composeColor.green + 0.114f * composeColor.blue)
                    val iconTint = if (luminance > 0.55f) Color.Black else Color.White
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
