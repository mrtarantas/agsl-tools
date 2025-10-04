package com.mrtarantas.agsl.mappers

import com.intellij.ui.JBColor
import com.mrtarantas.agsl.models.PreviewType
import com.mrtarantas.agsl.models.Uniform
import com.mrtarantas.agsl.models.UniformType
import com.mrtarantas.agsl.uistates.UniformPropertyUiState
import com.mrtarantas.agsl.uistates.UniformUiState

class UniformToUiStateMapper {
	operator fun invoke(uniform: Uniform, defaults: UniformUiState?): UniformPropertyUiState {
		return when (uniform.type) {
			UniformType.Float1 -> {
				UniformPropertyUiState(
					uniform.name,
					when (uniform.previewType) {
						PreviewType.Time -> UniformUiState.Time((defaults as? UniformUiState.Time)?.speed ?: 1f)
						else -> UniformUiState.Float1((defaults as? UniformUiState.Float1)?.value ?: 0f)
					}
				)
			}
			UniformType.Float2 -> {
				val float2Defaults = defaults as? UniformUiState.Float2
				UniformPropertyUiState(
					uniform.name,
					when (uniform.previewType) {
						PreviewType.Size -> UniformUiState.Size
						else -> UniformUiState.Float2(float2Defaults?.x ?: 0f, float2Defaults?.y ?: 0f)
					}
				)
			}
			UniformType.Float3 -> {
				val float3Defaults = defaults as? UniformUiState.Float3
				val colorDefaults = defaults as? UniformUiState.Rgb
				UniformPropertyUiState(
					uniform.name,
					when (uniform.previewType) {
						PreviewType.Color -> UniformUiState.Rgb(colorDefaults?.color ?: JBColor.WHITE)
						else -> UniformUiState.Float3(float3Defaults?.x ?: 0f, float3Defaults?.y ?: 0f, float3Defaults?.z ?: 0f)
					},
				)
			}
			UniformType.Float4 -> {
				val float4Defaults = defaults as? UniformUiState.Float4
				val colorDefaults = defaults as? UniformUiState.Argb
				UniformPropertyUiState(
					uniform.name,
					when (uniform.previewType) {
						PreviewType.Color -> UniformUiState.Argb(colorDefaults?.color ?: JBColor.WHITE)
						else -> UniformUiState.Float4(float4Defaults?.x ?: 0f, float4Defaults?.y ?: 0f, float4Defaults?.z ?: 0f, float4Defaults?.w ?: 0f)
					},
				)
			}
			UniformType.Shader -> {
				UniformPropertyUiState(
					uniform.name,
					UniformUiState.Shader((defaults as? UniformUiState.Shader)?.imageFile),
				)
			}
			UniformType.Bool1 -> {
				val boolDefaults = defaults as? UniformUiState.Bool1
				UniformPropertyUiState(
					uniform.name,
					UniformUiState.Bool1(boolDefaults?.value ?: false),
				)
			}
			UniformType.Bool2 -> {
				val bool2Defaults = defaults as? UniformUiState.Bool2
				UniformPropertyUiState(
					uniform.name,
					UniformUiState.Bool2(bool2Defaults?.x ?: false, bool2Defaults?.y ?: false),
				)
			}
			UniformType.Bool3 -> {
				val bool3Defaults = defaults as? UniformUiState.Bool3
				UniformPropertyUiState(
					uniform.name,
					UniformUiState.Bool3(bool3Defaults?.x ?: false, bool3Defaults?.y ?: false, bool3Defaults?.z ?: false),
				)
			}
			UniformType.Bool4 -> {
				val bool4Defaults = defaults as? UniformUiState.Bool4
				UniformPropertyUiState(
					uniform.name,
					UniformUiState.Bool4(bool4Defaults?.x ?: false, bool4Defaults?.y ?: false, bool4Defaults?.z ?: false, bool4Defaults?.w ?: false),
				)
			}
			UniformType.Int1 -> {
				val intDefaults = defaults as? UniformUiState.Int1
				UniformPropertyUiState(
					uniform.name,
					UniformUiState.Int1(intDefaults?.value ?: 0),
				)
			}
			UniformType.Int2 -> {
				val int2Defaults = defaults as? UniformUiState.Int2
				UniformPropertyUiState(
					uniform.name,
					UniformUiState.Int2(int2Defaults?.x ?: 0, int2Defaults?.y ?: 0),
				)
			}
			UniformType.Int3 -> {
				val int3Defaults = defaults as? UniformUiState.Int3
				UniformPropertyUiState(
					uniform.name,
					UniformUiState.Int3(int3Defaults?.x ?: 0, int3Defaults?.y ?: 0, int3Defaults?.z ?: 0),
				)
			}
			UniformType.Int4 -> {
				val int4Defaults = defaults as? UniformUiState.Int4
				UniformPropertyUiState(
					uniform.name,
					UniformUiState.Int4(int4Defaults?.x ?: 0, int4Defaults?.y ?: 0, int4Defaults?.z ?: 0, int4Defaults?.w ?: 0),
				)
			}
			UniformType.Mat2 -> {
				val mat2Defaults = defaults as? UniformUiState.Mat2
				UniformPropertyUiState(
					uniform.name,
					UniformUiState.Mat2(
						mat2Defaults?.m00 ?: 0f, mat2Defaults?.m01 ?: 0f,
						mat2Defaults?.m10 ?: 0f, mat2Defaults?.m11 ?: 0f,
					),
				)
			}
			UniformType.Mat3 -> {
				val mat2Defaults = defaults as? UniformUiState.Mat3
				UniformPropertyUiState(
					uniform.name,
					UniformUiState.Mat3(
						mat2Defaults?.m00 ?: 0f, mat2Defaults?.m01 ?: 0f, mat2Defaults?.m02 ?: 0f,
						mat2Defaults?.m10 ?: 0f, mat2Defaults?.m11 ?: 0f, mat2Defaults?.m12 ?: 0f,
						mat2Defaults?.m20 ?: 0f, mat2Defaults?.m21 ?: 0f, mat2Defaults?.m22 ?: 0f,
					),
				)
			}
			UniformType.Mat4 -> {
				val mat2Defaults = defaults as? UniformUiState.Mat4
				UniformPropertyUiState(
					uniform.name,
					UniformUiState.Mat4(
						mat2Defaults?.m00 ?: 0f, mat2Defaults?.m01 ?: 0f, mat2Defaults?.m02 ?: 0f, mat2Defaults?.m03 ?: 0f,
						mat2Defaults?.m10 ?: 0f, mat2Defaults?.m11 ?: 0f, mat2Defaults?.m12 ?: 0f, mat2Defaults?.m13 ?: 0f,
						mat2Defaults?.m20 ?: 0f, mat2Defaults?.m21 ?: 0f, mat2Defaults?.m22 ?: 0f, mat2Defaults?.m23 ?: 0f,
						mat2Defaults?.m30 ?: 0f, mat2Defaults?.m31 ?: 0f, mat2Defaults?.m32 ?: 0f, mat2Defaults?.m33 ?: 0f,
					),
				)
			}
		}
	}
}