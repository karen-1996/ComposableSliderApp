/*
 * Copyright (c) 04/03/2024 Karen Namalyan (namalyankaren@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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