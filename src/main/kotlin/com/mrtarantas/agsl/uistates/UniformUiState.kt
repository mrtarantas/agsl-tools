package com.mrtarantas.agsl.uistates

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor

sealed class UniformUiState {
	data class Shader(var imageFile: VirtualFile? = null) : UniformUiState()
	data class Float1(var value: Float = 0f) : UniformUiState()
	data class Float2(var x: Float = 0f, var y: Float = 0f) : UniformUiState()
	data class Float3(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) : UniformUiState()
	data class Float4(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f, var w: Float = 0f) : UniformUiState()
	data object Size : UniformUiState()
	data class Time(var speed: Float = 1f) : UniformUiState()
	data class Argb(var color: java.awt.Color = JBColor.white) : UniformUiState()
	data class Rgb(var color: java.awt.Color = JBColor.white) : UniformUiState()
	data class Bool1(var value: Boolean = false) : UniformUiState()
	data class Bool2(var x: Boolean = false, var y: Boolean = false) : UniformUiState()
	data class Bool3(var x: Boolean = false, var y: Boolean = false, var z: Boolean = false) : UniformUiState()
	data class Bool4(var x: Boolean = false, var y: Boolean = false, var z: Boolean = false, var w: Boolean = false) : UniformUiState()
	data class Int1(var value: Int = 0) : UniformUiState()
	data class Int2(var x: Int = 0, var y: Int = 0) : UniformUiState()
	data class Int3(var x: Int = 0, var y: Int = 0, var z: Int = 0) : UniformUiState()
	data class Int4(var x: Int = 0, var y: Int = 0, var z: Int = 0, var w: Int = 0) : UniformUiState()
	data class Mat2(
		var m00: Float = 0f, var m01: Float = 0f,
		var m10: Float = 0f, var m11: Float = 0f,
	) : UniformUiState()

	data class Mat3(
		var m00: Float = 0f, var m01: Float = 0f, var m02: Float = 0f,
		var m10: Float = 0f, var m11: Float = 0f, var m12: Float = 0f,
		var m20: Float = 0f, var m21: Float = 0f, var m22: Float = 0f,
	) : UniformUiState()

	data class Mat4(
		var m00: Float = 0f, var m01: Float = 0f, var m02: Float = 0f, var m03: Float = 0f,
		var m10: Float = 0f, var m11: Float = 0f, var m12: Float = 0f, var m13: Float = 0f,
		var m20: Float = 0f, var m21: Float = 0f, var m22: Float = 0f, var m23: Float = 0f,
		var m30: Float = 0f, var m31: Float = 0f, var m32: Float = 0f, var m33: Float = 0f,
	) : UniformUiState()
}