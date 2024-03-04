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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Preview
@Composable
fun SliderView(
    modifier: Modifier = Modifier,
    sliderConfigurator: SliderConfigApi = SliderConfigurator.TypeOverConfig(),
    onValueChange: (Float) -> Unit = {}
) {
    val initialValue: Float = sliderConfigurator.initialValue
    val sliderValueState = remember { mutableFloatStateOf(initialValue) }
    val sliderValueTextState = remember { mutableStateOf("${initialValue.roundToInt()}") }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = sliderConfigurator.sliderConfig.uiConfig.backgroundColor
    ) {
        when (sliderConfigurator) {
            is OverSliderConfigApi,
            is SliderConfigurator.TypeOverConfig -> {
                LinearTickSlider(
                    sliderValueState = sliderValueState,
                    sliderValueTextState = sliderValueTextState,
                    sliderConfigurator = sliderConfigurator
                )
                SliderWithLabel(
                    sliderValueTextState = sliderValueTextState,
                    sliderConfigurator = sliderConfigurator,
                    onValueChange = {
                        sliderValueState.floatValue = it
                        onValueChange.invoke(it)
                    })
            }

            is NonOverSliderConfigApi,
            is SliderConfigurator.TypeLinearConfig -> {
                LinearTickSlider(
                    sliderValueState,
                    sliderValueTextState,
                    sliderConfigurator = sliderConfigurator,
                    onValueChange = onValueChange
                )
            }

            is SliderConfigurator.TypeLinearAdditionalConfig -> {
                LinearTickSlider(
                    sliderValueState,
                    sliderValueTextState,
                    sliderConfigurator = sliderConfigurator,
                    onValueChange = onValueChange
                )
            }
        }
    }
}

@Composable
private fun SliderWithLabel(
    sliderValueTextState: State<String>,
    sliderConfigurator: SliderConfigApi,
    onValueChange: (Float) -> Unit,
) {
    val labelMinWidth: Dp = 23.9.dp
    val sliderValueState = remember {
        mutableFloatStateOf(sliderConfigurator.sliderConfig.scrolledValue)
    }
    val uiConfig = sliderConfigurator.sliderConfig.uiConfig
    Column(
        modifier =
        Modifier
            .background(Color.Transparent)
            .height(56.dp)
            .padding(top = 16.dp)
    ) {
        val heightOfText = remember {
            mutableStateOf(0.dp)
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val offset =
                getSliderOffset(
                    value = sliderValueState.floatValue,
                    sliderConfigurator = sliderConfigurator,
                    boxWidth = maxWidth,
                    labelWidth = labelMinWidth
                )
            val currentDensity = LocalDensity.current

            SliderLabel(
                modifier = Modifier
                    .padding(start = offset)
                    .onGloballyPositioned { coordinates ->
                        heightOfText.value = with(currentDensity) { coordinates.size.height.toDp() }
                    },
                label = sliderValueTextState.value,
                minWidth = labelMinWidth,
                labelColor = if (sliderValueState.floatValue.round(2) > uiConfig.overvalueIndex) {
                    uiConfig.overvalueSelectedColor
                } else {
                    uiConfig.thumbColor
                },
                labelBackgroundColor = uiConfig.labelBackgroundColor,
            )
        }

        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = uiConfig.barHeightMax - (heightOfText.value - uiConfig.barHeightMax), start = 2.dp, end = 2.dp),
            value = sliderValueState.floatValue,
            onValueChange = {
                sliderValueState.floatValue = it
                onValueChange(it)
            },
            valueRange = sliderConfigurator.sliderConfig.range,
            colors =
            SliderDefaults.colors(
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent,
                thumbColor = if (sliderValueState.floatValue.round(2) > uiConfig.overvalueIndex) {
                    uiConfig.overvalueSelectedColor
                } else {
                    uiConfig.thumbColor
                },
            ),
        )
    }
}

@Composable
private fun SliderLabel(
    label: String,
    minWidth: Dp,
    modifier: Modifier = Modifier,
    labelColor: Color,
    labelBackgroundColor: Color
) {
    Text(
        modifier =
        modifier
            .defaultMinSize(minWidth = minWidth)
            .background(color = labelBackgroundColor, shape = RoundedCornerShape(6.dp)),
        text = label,
        textAlign = TextAlign.Center,
        color = labelColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1
    )
}

private fun getSliderOffset(
    value: Float,
    sliderConfigurator: SliderConfigApi,
    boxWidth: Dp,
    labelWidth: Dp
): Dp {
    val valueRange = sliderConfigurator.sliderConfig.range
    val coerced = value.coerceIn(valueRange.start, valueRange.endInclusive)
    val positionFraction = calcFraction(valueRange.start, valueRange.endInclusive, coerced)

    return (boxWidth - labelWidth) * positionFraction
}

private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)
