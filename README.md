# AGSL Tools — AGSL Preview and Language Support for Android Studio

[![Version](https://img.shields.io/jetbrains/plugin/v/28637-agsl-tools.svg)](https://plugins.jetbrains.com/plugin/28637-agsl-tools)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/28637-agsl-tools.svg)](https://plugins.jetbrains.com/plugin/28637-agsl-tools)
[![Rating](https://img.shields.io/jetbrains/plugin/r/rating/28637-agsl-tools.svg)](https://plugins.jetbrains.com/plugin/28637-agsl-tools)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

<img width="49%" alt="agsl" src="https://github.com/user-attachments/assets/46ce1adc-e3ce-4af8-b757-f0b3cb4d28f2" />
<img width="49%" alt="agsl-demo" src="https://github.com/user-attachments/assets/d701aff6-c692-482b-96cc-b68de1274ee9" />

**AGSL Tools** is an Android Studio plugin for working with AGSL shaders. It adds a live preview for `.agsl` files with automatic uniform controls and compiler error reporting.

It also brings full editor support: syntax highlighting, code completion, formatting, rename refactoring, find usages, parameter hints, and short inline documentation for built-in functions.

> AGSL (Android Graphics Shading Language) is the shader language used by `RuntimeShader`, available starting from Android 13 (API 33).

## Features

### Live shader preview

AGSL Tools shows a real-time preview for AGSL shaders directly in Android Studio.
When the shader code is valid, the preview updates automatically.
When the shader code is invalid, the plugin shows the compiler error message and the line where the error occurred.

### Uniform controls

Uniform parameters are detected automatically and shown in the preview panel, so you can test shader inputs without writing additional Android code.

Supported controls:

| Uniform type               | Preview control                                                     |
| -------------------------- | ------------------------------------------------------------------- |
| `shader`                   | Image picker                                                        |
| Scalar and vector uniforms | Numeric input fields                                                |
| `float3`, `float4`         | Numeric input fields or color picker                                |
| `float2`                   | Numeric input fields or canvas size binding                         |
| `float`                    | Numeric input field or current time binding with configurable speed |

### AGSL editor support

* **Syntax highlighting**
* **Code completion**
* **Code formatting**
* **Rename refactoring** for variables and functions
* **Find usages**
* **Function parameter hints**
* **Short inline documentation** for built-in functions

## Requirements

* **Android Studio Otter (2025.2.1)** or newer. The plugin targets IntelliJ Platform build `252` with no upper bound, so it also works on later versions.
* AGSL shaders target Android 13 (API 33) and above, since `RuntimeShader` is unavailable on earlier versions.

The preview is rendered with [Skia](https://skia.org/) via [Skiko](https://github.com/JetBrains/skiko) (`org.jetbrains.skiko:skiko-awt`), so the output is rendered inside the IDE and does not require a connected device or emulator.

## Installation

1. Open Android Studio
2. Go to `Settings` → `Plugins` → `Marketplace`
3. Search for `AGSL Tools`
4. Install and restart the IDE

Alternatively, install it from the [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/28637-agsl-tools).

## Usage

You can open an existing `.agsl` file or create a new one from Android Studio.

To create a new AGSL file:

1. Right-click the target folder, preferably `res/raw`
2. Select `New` → `AGSL Shader`
3. Enter the file name
4. Start writing the shader code

To preview the shader:

1. Open an `.agsl` file
2. Use the preview panel to see the shader output
3. Edit uniform values in the generated controls
4. Check compiler errors in the preview panel when the code is invalid

## Use cases

* Prototyping and testing AGSL shaders without a sample app
* Debugging shader compilation errors
* Tweaking uniforms — colors, time, image inputs — and seeing the result instantly

## License

This project is licensed under the [Apache 2.0 License](LICENSE).

## Contributing

Contributions are welcome. Feel free to open issues or submit pull requests.
