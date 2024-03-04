package com.composablesliders

import android.annotation.SuppressLint
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.round
import kotlin.math.roundToInt

@Composable
private fun rememberSliderState(
    currentValue: Float,
    range: ClosedFloatingPointRange<Float>,
    allowSnap: Boolean,
    additionalTick: Boolean
): LinearTickSliderState {
    val state =
        rememberSaveable(saver = LinearTickSliderStateImpl.Saver) {
            LinearTickSliderStateImpl(
                currentValue, range, allowSnap, additionalTick = additionalTick
            )
        }
    if (allowSnap) {
        LaunchedEffect(key1 = Unit) { state.snapTo(state.currentValue.roundToInt().toFloat()) }
    }
    return state
}

@SuppressLint("ReturnFromAwaitPointerEventScope", "MultipleAwaitPointerEventScopes")
private fun Modifier.drag(state: LinearTickSliderState, numSegments: Int, segmentWidthPx: Int) =
    pointerInput(Unit) {
        val decay = splineBasedDecay<Float>(this)
        //    val segmentWidthPx = size.width / numSegments
        coroutineScope {
            while (isActive) {
                val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                state.stop()
                val tracker = VelocityTracker()
                awaitPointerEventScope {
                    state.allowHaptic = true
                    horizontalDrag(pointerId) { change ->
                        val horizontalDragOffset =
                            state.currentValue - change.positionChange().x / segmentWidthPx
                        launch { state.snapTo(horizontalDragOffset) }
                        tracker.addPosition(change.uptimeMillis, change.position)
                        change.consume()
                    }
                }
                val velocity = tracker.calculateVelocity().x / numSegments
                val targetValue = decay.calculateTargetValue(state.currentValue, -velocity)
                launch { state.decayTo(velocity, targetValue) }
            }
        }
    }

internal fun Float.round(decimals: Int): Float {
    var multiplier = 1.0f
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

@Composable
private fun LinearTickSlider(
    modifier: Modifier = Modifier,
    linearTickValueState: LinearTickSliderState,
    numSegments: Int =
        (linearTickValueState.range.endInclusive - linearTickValueState.range.start + 1).toInt(),
    sliderValueState: MutableFloatState,
    sliderValueTextState: MutableState<String>,
    sliderConfigurator: SliderConfigApi,
    onValueChange: (Float) -> Unit
) {
    val ranges = sliderConfigurator.sliderConfig.ranges
    val coefficients = sliderConfigurator.sliderConfig.coefficients
    val allowDrag = sliderConfigurator.sliderConfig.allowDrag
    val uiConfig = sliderConfigurator.sliderConfig.uiConfig
    val barHeightMin = uiConfig.barHeightMin
    val barHeightMax = uiConfig.barHeightMax
    val minAlphaAnim = uiConfig.minAlphaAnim

    Column(modifier = modifier, horizontalAlignment = CenterHorizontally) {
        var boxWithConstraintsModifier = Modifier.fillMaxWidth()
        boxWithConstraintsModifier =
            if (allowDrag) {
                boxWithConstraintsModifier.drag(
                    linearTickValueState, numSegments, sliderConfigurator.tickOffset.px
                )
            } else {
                boxWithConstraintsModifier.padding(horizontal = 6.dp)
            }
        BoxWithConstraints(
            modifier = boxWithConstraintsModifier,
            contentAlignment = Alignment.TopCenter,
        ) {
            val segmentWidth =
                if (allowDrag) {
                    sliderConfigurator.tickOffset.dp
                } else {
                    maxWidth / numSegments
                }

            val segmentWidthPx =
                if (allowDrag) {
                    sliderConfigurator.tickOffset.px.toFloat()
                } else {
                    constraints.maxWidth.toFloat() / numSegments.toFloat()
                }
            val currentValue: Float = linearTickValueState.currentValue
            val segments = numSegments + 1
            val start =
                (currentValue - segments)
                    .toInt()
                    .coerceAtLeast(linearTickValueState.range.start.toInt())
            val end =
                (currentValue + segments)
                    .toInt()
                    .coerceAtMost(linearTickValueState.range.endInclusive.toInt())

            val previousHapticValue = remember { mutableIntStateOf(0) }
            var currentHapticValue = start
            val maxOffset = constraints.maxWidth / 2f
            for (i in start..end) {
                val offsetX =
                    (i - currentValue) * segmentWidthPx +
                            (if ((i == end) && sliderConfigurator.sliderConfig.additionalTick) {
                                sliderConfigurator.additionalTickWidth.px
                            } else {
                                0f
                            })
                val alpha = 1f - (1f - minAlphaAnim) * (offsetX / maxOffset).absoluteValue
                Text(
                    modifier =
                    Modifier.graphicsLayer(
                        translationX = offsetX,
                        alpha = alpha,
                    ),
                    text =
                    if (i % uiConfig.step == 0) {
                        if (sliderConfigurator is SliderConfigurator.TypeLinearAdditionalConfig) {
                            "${sliderConfigurator.valueCalculator(i.toFloat(), ranges).round(2)}"
                        } else {
                            "${
                                sliderConfigurator.valueCalculator(i.toFloat(), ranges).roundToInt()
                            }"
                        }
                    } else {
                        ""
                    },
                    fontSize = 10.sp,
                    color = uiConfig.labelColor,
                    fontWeight = FontWeight.SemiBold,
                )
                Column(
                    modifier =
                    Modifier
                        .width(segmentWidth)
                        .graphicsLayer(alpha = alpha, translationX = offsetX)
                        .align(Center),
                    horizontalAlignment = CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    val coefficient =
                        if (allowDrag) {
                            1f
                        } else {
                            sliderConfigurator.coefficientCalculator(
                                sliderValueState.floatValue, coefficients
                            )
                        }

                    val color =
                        if (allowDrag) {
                            if (i % uiConfig.step == 0) {
                                uiConfig.highlightItemColor
                            } else {
                                uiConfig.barColor
                            }
                        } else {
                            if (i * coefficient <= (sliderValueState.floatValue * coefficient).roundToInt()) {
                                currentHapticValue++
                                if (i > uiConfig.overvalueIndex) {
                                    uiConfig.overvalueSelectedColor
                                } else {
                                    uiConfig.selectedColor
                                }
                            } else if (i > uiConfig.overvalueIndex) {
                                uiConfig.overvalueUnselectedColor
                            } else if (i % uiConfig.step == 0) {
                                uiConfig.highlightItemColor
                            } else {
                                uiConfig.barColor
                            }
                        }

                    if (allowDrag &&
                        (i * coefficient).toInt() <= (currentValue * coefficient).toInt()
                    ) {
                        if (i == end && sliderConfigurator.sliderConfig.additionalTick) {
                            if (currentValue >=
                                sliderConfigurator.sliderConfig.range.endInclusive
                            ) {
                                currentHapticValue++
                            }
                        } else {
                            currentHapticValue++
                        }
                    }

                    Box(
                        modifier =
                        Modifier
                            .width(uiConfig.barWidth)
                            .height(
                                if (i % uiConfig.step == 0) {
                                    barHeightMax
                                } else {
                                    barHeightMin
                                }
                            )
                            .background(color = color, shape = RoundedCornerShape(2.dp))
                            .align(CenterHorizontally)
                    )
                }
            }

            val value = if (allowDrag) currentValue else sliderValueState.floatValue
            val haptic = LocalHapticFeedback.current

            if (currentHapticValue != previousHapticValue.intValue &&
                linearTickValueState.allowHaptic &&
                sliderConfigurator.sliderConfig.allowHaptic &&
                !(previousHapticValue.intValue == end + 1 && currentValue >= end - 1)
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                previousHapticValue.intValue = currentHapticValue
            }

            // do it after first composition to setup haptic correctly
            if (sliderConfigurator is SliderConfigurator.TypeOverConfig) {
                linearTickValueState.allowHaptic = true
            }

            sliderValueTextState.value =
                "${sliderConfigurator.valueCalculator(value, ranges).roundToInt()}"
        }
    }

    if (allowDrag) {
        BoxWithConstraints {
            this.constraints
            onValueChange.invoke(linearTickValueState.currentValue)
        }

        ScrolledValue(
            sliderConfigurator = sliderConfigurator,
            linearTickValueState = linearTickValueState,
            ranges = ranges
        )
    }
}

@Composable
private fun ScrolledValue(
    modifier: Modifier = Modifier,
    sliderConfigurator: SliderConfigApi,
    linearTickValueState: LinearTickSliderState,
    ranges: List<Pair<ClosedFloatingPointRange<Float>, (value: Float) -> Float>>
) {
    val uiConfig = sliderConfigurator.sliderConfig.uiConfig
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = CenterHorizontally,
    ) {
        BoxWithConstraints {
            this.constraints
            Text(
                modifier = Modifier.background(
                    color = uiConfig.labelBackgroundColor,
                    shape = RoundedCornerShape(6.dp)
                ),
                text = "${
                    sliderConfigurator.valueCalculator(linearTickValueState.currentValue, ranges)
                        .round(
                            if (sliderConfigurator is SliderConfigurator.TypeLinearAdditionalConfig) 2 else 1
                        )
                }",
                fontSize = 12.sp,
                color = uiConfig.selectedColor,
                fontWeight = FontWeight.SemiBold
            )
        }
//        Spacer(modifier = Modifier.height(1.dp))
        Box(
            modifier = Modifier
                .background(
                    color = uiConfig.selectedColor,
                    shape = RoundedCornerShape(uiConfig.barWidth)
                )
                .height(uiConfig.barHeightMax + 6.dp)
                .width(uiConfig.barWidth * 2),
        )
    }
}

@Preview(widthDp = 720, showBackground = true)
@Composable
internal fun LinearTickSlider(
    sliderValueState: MutableFloatState = mutableFloatStateOf(0f),
    sliderValueTextState: MutableState<String> = mutableStateOf(""),
    sliderConfigurator: SliderConfigApi = SliderConfigurator.TypeOverConfig(),
    onValueChange: (Float) -> Unit = {}
) {
    val linearTickSliderState =
        rememberSliderState(
            sliderConfigurator.sliderConfig.currentValue,
            sliderConfigurator.sliderConfig.range,
            allowSnap = sliderConfigurator.sliderConfig.allowSnap,
            additionalTick = sliderConfigurator.sliderConfig.additionalTick
        )
    LinearTickSlider(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .padding(vertical = 16.dp),
        sliderValueState = sliderValueState,
        sliderValueTextState = sliderValueTextState,
        linearTickValueState = linearTickSliderState,
        sliderConfigurator = sliderConfigurator,
        onValueChange = onValueChange
    )
}
