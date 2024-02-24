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
import com.composablesliders.SliderConfigurator
import com.composablesliders.SliderView
import com.composablesliders.api.SliderConfigApi

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

data class MyConfig(override val initialValue: Float = 1f, override val sliderConfig: SliderConfig) : SliderConfigApi

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