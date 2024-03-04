package com.composablesliders

sealed interface SliderConfigApi {
    val initialValue: Float
    val sliderConfig: SliderConfig
    val tickOffset: Int
    val additionalTickWidth: Float

    fun valueCalculator(
        currentValue: Float,
        ranges: List<Pair<ClosedFloatingPointRange<Float>, (Float) -> Float>>
    ): Float = ranges.find { it.first.contains(currentValue) }?.second?.invoke(currentValue)
        ?: valueCalculator(
            currentValue.coerceIn(
                ranges.first().first.start,
                ranges.last().first.endInclusive
            ), ranges
        )

    fun coefficientCalculator(
        currentValue: Float,
        ranges: List<Pair<ClosedFloatingPointRange<Float>, Float>>
    ): Float = ranges.find { it.first.contains(currentValue) }?.second ?: 1f
}

interface OverSliderConfigApi : SliderConfigApi

interface NonOverSliderConfigApi : SliderConfigApi