package com.composablesliderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composablesliderapp.ui.theme.ComposableSliderAppTheme
import com.composablesliders.SliderConfig
import com.composablesliders.SliderView
import com.composablesliders.NonOverSliderConfigApi
import com.composablesliders.SliderConfigurator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    Column(
                        modifier = Modifier.background(Color(0xFF262626))
                    ) {
                        SliderView(
                            sliderConfigurator = MyConfig(
                                sliderConfig = SliderConfig(
                                    listOf(
                                        Pair(0f..20f, 0f..100f),
                                        Pair(20f..30f, 100f..200f),
                                        Pair(30f..40f, 200f..400f),
                                    ).map { interpolationRange ->
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
                                    listOf(
                                        Pair(0f..20f, 0f..100f),
                                        Pair(20f..30f, 100f..200f),
                                        Pair(30f..40f, 200f..400f),
                                    ).map {
                                    Pair(it.first, 1f)
                                }
                                )
                            )
                        )

                        Spacer(modifier = Modifier.height(2.dp))
                        SliderView(
                            sliderConfigurator = SliderConfigurator.TypeOverConfig()
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        SliderView(
                            sliderConfigurator = SliderConfigurator.TypeLinearConfig()
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        SliderView(
                            sliderConfigurator = SliderConfigurator.TypeLinearAdditionalConfig()
                        )
                    }
                }
            }
        }
    }
}

data class MyConfig(
    override val initialValue: Float = 1f,
    override val sliderConfig: SliderConfig
) : NonOverSliderConfigApi {
    override val tickOffset: Int = 60
    override val additionalTickWidth: Float = 0f
}

private fun linearInterpolation(x: Float, x1: Float, x2: Float, y1: Float, y2: Float): Float =
    y1 + (y2 - y1) / (x2 - x1) * (x - x1)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposableSliderAppTheme {
        Greeting("Android")
    }
}