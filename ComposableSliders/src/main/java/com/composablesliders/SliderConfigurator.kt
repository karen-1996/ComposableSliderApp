package com.composablesliders

import android.content.res.Resources
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composablesliders.SliderConfigurator.Companion.BarHeightMax
import com.composablesliders.SliderConfigurator.Companion.BarHeightMin
import com.composablesliders.SliderConfigurator.Companion.BarWidth

private fun linearInterpolation(x: Float, x1: Float, x2: Float, y1: Float, y2: Float): Float =
    y1 + (y2 - y1) / (x2 - x1) * (x - x1)

sealed class SliderConfigurator(
    protected open val start: Float,
    protected open val end: Float
) : SliderConfigApi {

    internal abstract val interpolationRanges: List<Pair<ClosedFloatingPointRange<Float>, ClosedFloatingPointRange<Float>>>

    data class TypeOverConfig(
        override val initialValue: Float = 0f,
        override val start: Float = 0f,
        override val end: Float = 40f,
        override val interpolationRanges: List<Pair<ClosedFloatingPointRange<Float>, ClosedFloatingPointRange<Float>>> = listOf(
            Pair(start..20f, 0f..100f),
            Pair(20f..30f, 100f..200f),
            Pair(30f..end, 200f..400f),
        ),
        override val sliderConfig: SliderConfig = SliderConfig(
            ranges = interpolationRanges.map { interpolationRange ->
                Pair(interpolationRange.first) {
                    linearInterpolation(
                        it,
                        interpolationRange.first.start,
                        interpolationRange.first.endInclusive,
                        interpolationRange.second.start,
                        interpolationRange.second.endInclusive
                    )
                }
            },
            coefficients = interpolationRanges.map {
                Pair(
                    it.first,
                    (it.second.endInclusive - it.second.start) / (it.first.endInclusive - it.first.start)
                )
            },
            scrolledValue = initialValue,
            allowDrag = false
        )
    ) : SliderConfigurator(start, end) {
        override val tickOffset: Int = 60
        override val additionalTickWidth: Float = 0f
    }

    data class TypeLinearConfig(
        override val initialValue: Float = 0f,
        override val start: Float = 0f,
        override val end: Float = 15f,
        override val tickOffset: Int = 12,
        override val interpolationRanges: List<Pair<ClosedFloatingPointRange<Float>, ClosedFloatingPointRange<Float>>> = listOf(
            Pair(start..end, 0f..45f),
//            Pair(20f..30f, 100f..200f),
//            Pair(30f..end, 200f..400f),
        ),
        override val sliderConfig: SliderConfig = SliderConfig(
            ranges = interpolationRanges.map { interpolationRange ->
                Pair(interpolationRange.first) {
                    linearInterpolation(
                        it,
                        interpolationRange.first.start,
                        interpolationRange.first.endInclusive,
                        interpolationRange.second.start,
                        interpolationRange.second.endInclusive
                    )
                }
            },
            coefficients = interpolationRanges.map {
                Pair(it.first, 5f)
            },
            currentValue = initialValue,
            uiConfig = SliderUiConfig(
                barHeightMin = BarHeightMiddle,
                barHeightMax = BarHeightMiddle,
                barWidth = BarWidth,
                minAlphaAnim = MinAlpha
            ),
            scrolledValue = initialValue,
            additionalTick = false
        )
    ) : SliderConfigurator(start, end) {
        override val additionalTickWidth: Float = 0f
    }

    data class TypeLinearAdditionalConfig(
        override val initialValue: Float = 0f,
        override val start: Float = 0f,
        override val end: Float = 42f,
        override val tickOffset: Int = 12,
        override val additionalTickWidth: Float = (5.38f - 5.2f) * tickOffset / (8f / end),
        override val interpolationRanges: List<Pair<ClosedFloatingPointRange<Float>, ClosedFloatingPointRange<Float>>> = listOf(
            Pair(start..5f, 0.1f..0.25f),
            Pair(5f..20f, 0.25f..1f),
            Pair(20f..end - 1, 1f..5.2f),
            Pair(end - 1..end + (additionalTickWidth + tickOffset) / tickOffset - 1f, 5.2f..5.38f)
        ),
        override val sliderConfig: SliderConfig = SliderConfig(
            ranges = interpolationRanges.map { interpolationRange ->
                Pair(interpolationRange.first) {
                    linearInterpolation(
                        it,
                        interpolationRange.first.start,
                        interpolationRange.first.endInclusive,
                        interpolationRange.second.start,
                        interpolationRange.second.endInclusive
                    )
                }
            },
            coefficients = interpolationRanges.map {
                Pair(it.first, 1f)
            },
            uiConfig = SliderUiConfig(
                barHeightMin = BarHeightMiddle,
                barHeightMax = BarHeightMiddle,
                barWidth = BarWidth,
                minAlphaAnim = MinAlpha,
            ),
            allowSnap = true,
            scrolledValue = initialValue,
            currentValue = initialValue,
            additionalTick = true
        )
    ) : SliderConfigurator(start, end)

    companion object {
        internal val BarWidth = 4.dp
        internal val BarHeightMax = 20.dp
        internal val BarHeightMin = 12.dp
        private val BarHeightMiddle = 16.dp
        private const val MinAlpha = 0.1f
    }
}

data class SliderConfig(
    val ranges: List<Pair<ClosedFloatingPointRange<Float>, (value: Float) -> Float>>,
    val coefficients: List<Pair<ClosedFloatingPointRange<Float>, Float>>,
    val scrolledValue: Float = 0f,
    val range: ClosedFloatingPointRange<Float> = ranges.first().first.start..ranges.last().first.endInclusive,
    val allowSnap: Boolean = false,
    val allowHaptic: Boolean = true,
    val allowDrag: Boolean = true,
    val currentValue: Float = (range.endInclusive - range.start) / 2,
    val uiConfig: SliderUiConfig = SliderUiConfig(
        barHeightMin = BarHeightMin,
        barHeightMax = BarHeightMax,
        barWidth = BarWidth,
        minAlphaAnim = 1f,
    ),
    val additionalTick: Boolean = false
)

data class SliderUiConfig(
    val barHeightMin: Dp,
    val barHeightMax: Dp,
    val barWidth: Dp,
    val minAlphaAnim: Float,
    val isDark: Boolean = true,
    val backgroundColor: Color = Color.Transparent,
    val highlightItemColor: Color = Color(0xFFB19C9C),
    val barColor: Color = Color(0xFF424242),
    val overvalueSelectedColor: Color = Color(0xFFF30B42),
    val overvalueUnselectedColor: Color = Color(0x4DAC042C),
    val labelColor: Color = Color(0xFF5E4A4A),
    val labelBackgroundColor: Color = Color(0xE6262626),
    val selectedColor: Color = Color(0xFF3C6FD1),
    val thumbColor: Color = Color(0xFF3C6FD1),
    val overvalueIndex: Int = 35,
    val step: Int = 5,
)

internal val Float.px: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

internal val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
