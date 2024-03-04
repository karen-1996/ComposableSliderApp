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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import kotlin.math.roundToInt

@Stable
internal interface LinearTickSliderState {
    val currentValue: Float
    val range: ClosedFloatingPointRange<Float>
    var allowHaptic: Boolean

    suspend fun snapTo(value: Float)
    suspend fun decayTo(velocity: Float, value: Float)
    suspend fun stop()
}

internal class LinearTickSliderStateImpl(
    currentValue: Float,
    override val range: ClosedFloatingPointRange<Float>,
    private val allowSnap: Boolean,
    private val additionalTick: Boolean,
    override var allowHaptic: Boolean = false
) : LinearTickSliderState {
    private val intRange: IntRange = range.start.toInt()..range.endInclusive.toInt()
    private val animatedly = Animatable(currentValue)
    private val decayAnimationSpec = FloatTweenSpec(
        easing = LinearEasing,
    )

    override val currentValue: Float
        get() = animatedly.value

    override suspend fun stop() {
        animatedly.stop()
    }

    override suspend fun snapTo(value: Float) {
        if (additionalTick) {
            animatedly.snapTo(value.coerceIn(range.start - 0.5f..range.endInclusive * 2f + 0.5f))
        } else {
            animatedly.snapTo(value.coerceIn(range.start - 0.5f..range.endInclusive + 0.5f))
        }
    }

    override suspend fun decayTo(velocity: Float, value: Float) {
        val target =
            if (allowSnap) {
                if (value > range.endInclusive.toInt() - 1f && additionalTick) {
                    if (value > range.endInclusive - (range.endInclusive - (range.endInclusive.toInt() - 1f)) / 2f) {
                        range.endInclusive
                    } else {
                        range.endInclusive.toInt() - 1f
                    }
                } else {
                    value.roundToInt().coerceIn(intRange).toFloat()
                }
            } else {
                if (additionalTick) {
                    value.coerceIn(range.start - 0.5f..range.endInclusive * 2f + 0.5f)
                } else {
                    value.coerceIn(range.start..range.endInclusive)
                }
            }

        animatedly.animateTo(
            targetValue = target,
            initialVelocity = velocity,
            animationSpec = decayAnimationSpec,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LinearTickSliderStateImpl

        if (range != other.range) return false
        if (intRange != other.intRange) return false
        if (animatedly != other.animatedly) return false
        return decayAnimationSpec == other.decayAnimationSpec
    }

    override fun hashCode(): Int {
        var result = range.hashCode()
        result = 31 * result + intRange.hashCode()
        result = 31 * result + animatedly.hashCode()
        result = 31 * result + decayAnimationSpec.hashCode()
        return result
    }

    companion object {
        val Saver = Saver<LinearTickSliderStateImpl, List<Any>>(
            save = { listOf(it.currentValue, it.range.start, it.range.endInclusive, it.allowSnap, it.additionalTick) },
            restore = {
                LinearTickSliderStateImpl(
                    currentValue = checkNotNull(it[0] as? Float),
                    range = checkNotNull(it[1] as? Float)..checkNotNull(it[2] as? Float),
                    allowSnap = checkNotNull(it[3] as? Boolean),
                    additionalTick = checkNotNull(it[4] as Boolean)
                )
            }
        )
    }
}
