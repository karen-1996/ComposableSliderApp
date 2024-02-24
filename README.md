
# Composable Sliders Library for Android

This library provides customizable Composable Sliders for Android, allowing you to easily create sliders with various configurations such as size, colors, ranges, and more.

## Usage

Here is an example of how you can use the library to create different types of sliders:

```kotlin
SliderView(
    sliderConfigurator = SliderConfigurator.VolumeConfig()
)
Spacer(modifier = Modifier.height(2.dp))
SliderView(
    sliderConfigurator = SliderConfigurator.FadeConfig()
)
Spacer(modifier = Modifier.height(2.dp))
SliderView(
    sliderConfigurator = SliderConfigurator.SpeedConfig()
)
```

## SliderView Composable

The SliderView composable is the main component of this library. It allows you to create a slider with a specific configuration. Here's how you can use it:

```
@Composable
fun SliderView(
    modifier: Modifier = Modifier,
    sliderConfigurator: SliderConfigurator = SliderConfigurator.VolumeConfig(),
    onValueChange: (Float) -> Unit = {}
) {
    // Implementation details...
}
```

## Installation

To use this library in your Android project, add the following dependency to your build.gradle file:

```
implementation "com.example:composablesliders:1.0.0"
```

## License
This library is licensed under the MIT License. See the LICENSE file for details.