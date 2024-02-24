package com.composablesliders.api

import com.composablesliders.SliderConfig

interface SliderConfigApi {
    val initialValue: Float
    val sliderConfig: SliderConfig
}