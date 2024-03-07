[![Maven Central](https://img.shields.io/maven-central/v/io.github.karen-1996.composableviews/composablesliders)](https://central.sonatype.com/artifact/io.github.karen-1996.composableviews/composablesliders/)

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

All these configs are configurable too, you can pass all the parameters you want. But if you want to
add your own config you can do it easily. All you need to do is create a ```data``` class and 
implement one of these two interfaces.
1. ```OverSliderConfigApi```
![alt text](https://ucc53d65f132b35cb97df6fe0108.previews.dropboxusercontent.com/p/thumb/ACP_FW7iVIVr8bcYNgWLEIIu7-j9WEOu0HkkGKJOsjpoBLl2q_YzkOS-rg7Q9y1_YRkCDuJNUOAHVdnMUrdIoaI2QQ4c2BmwYSphQbIh8Whwk4_5NyOlG1hI2aVCXWMSJy_q__SXi_Rz3bz7fgLyjaHBiy44VTQeEyjudQNaY8zmfYnigPlzAyi-Y59HJozb8k74d8aG8A8TFZPbZ1uFqVz2wS_l0cVPPedI42zbFTOqJgZHxaCmg_9y6LbU5EwJMgFUXTeQin-s7NivftZKOYQvtCTD0oPLsuXxSBpyD-0GoW9jWsK38zcw-SYG3qECcjzMIhcfdyHZOQtvVd4-riRWNfFnSS4cuTSDD3ZM6p5AugBBnNVNve0-qFYi4cpzK4E/p.png)

2. ```NonOverSliderConfigApi```
![alt text](https://ucb9696a7ce03a19aa65d72d4721.previews.dropboxusercontent.com/p/thumb/ACPHSCw-y3Vhi8TTnyFwUpY3DwwnvygEapwzmhlJ0dBIaVjtJkgyErv5A4QWmwzo2TPcXmRVFucZAw5C2wf9x5LloFtXfc1maVNkGnIXo9VoQL-1bSKLLj7zsIN0LYXlhjLChblaWWU0PiKwaqOXEv-5OP7dsNZpKmczxtit6jHijOiLMCrB3QgS_ScwsF0E3odXt9oe38e1pQwz8dpduyNhgx4ipLQ84FbFl2Y3Mp0hVNUP_3Q3PrVX8lCBcdAf8sOUrKPZNs7IZr5hdDiljk3rs2vSIW-6UF1xs6A10t3ENE_aa5VNHP0yXJMh0gKrkyQzJCQGbnNuFLisQHrb-JGgvrykpDxoXr92H5PTJMMis0hJyscV3cVhlYZexVDN_pI/p.png)

Example of Custom Config
```kotlin
data class MyConfig(
    override val initialValue: Float = 1f,
    override val sliderConfig: SliderConfig
) : NonOverSliderConfigApi {
    override val tickOffset: Int = 60
    override val additionalTickWidth: Float = 0f
}
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

