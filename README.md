[![Maven Central](https://img.shields.io/maven-central/v/io.github.karen-namalyan.composableviews/composablesliderds)](https://central.sonatype.com/artifact/io.github.karen-1996.composableviews/ComposableSliders/)

# Composable Sliders Library for Android

This library provides customizable Composable Sliders for Android, allowing you to easily create sliders with various configurations such as size, colors, ranges, and more.

## Navigation
- [Usage](#usage)
- [SliderView Composable](#sliderview-composable)
- [Installation](#installation)
- [Feedback and contact us](#contact-author)
- [License](#license)
- [Contribute](#contributing)


## Usage

Here is an example of how you can use the library to create different types of sliders:

```kotlin
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
```

### Custom Config

Currently we provide you 3 different existing configs to create your slider
1. ```SliderConfigurator.TypeOverConfig```
2. ```SliderConfigurator.TypeLinearConfig```
3. ```SliderConfigurator.TypeLinearAdditionalConfig```

All these configs are configurable too, you can pass all the parameters you want.
But if you want to add your own config you can do it easily. All you need to create a ```data``` class 
and implement from ```SliderConfigApi``` interface.

Example of Custom Config
```kotlin
data class MyConfig(override val initialValue: Float = 1f, override val sliderConfig: SliderConfig) : SliderConfigApi
```

## SliderView Composable

The SliderView composable is the main component of this library. It allows you to create a slider with a specific configuration. Here's how you can use it:

```kotlin
@Composable
fun SliderView(
    modifier: Modifier = Modifier,
    sliderConfigurator: SliderConfigurator = SliderConfigurator.TypeOverConfig(),
    onValueChange: (Float) -> Unit = {}
) {
    // Implementation details...
}
```

## Installation

To use this library in your Android project, add the following dependency to your build.gradle file:

```kotlin
implementation("io.github.karen-1996.composableviews:composablesliders:1.0.0") // take latest from Maven central
```

## Contact Author
- **Email:** [namalyankaren@gmail.com](mailto:namalyankaren@gmail.com)
- **LinkedIn:** [Karen Namalyan](https://www.linkedin.com/in/karen-namalyan/)


## License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

## Contributing

If you would like to contribute code you can do so through GitHub by forking the repository and sending a pull request.
When submitting code, please make every effort to follow existing conventions and style in order to keep the code as readable as possible.

