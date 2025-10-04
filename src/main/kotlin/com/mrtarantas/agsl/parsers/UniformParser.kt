package com.mrtarantas.agsl.parsers

import com.mrtarantas.agsl.models.PreviewType
import com.mrtarantas.agsl.models.Uniform
import com.mrtarantas.agsl.models.UniformType

class UniformParser {
	private val previewLineRe = Regex(
		pattern = """^\s*//\s*previewtype\s*:\s*([A-Za-z_][A-Za-z0-9_\- ]*)\s*$""",
		options = setOf(RegexOption.IGNORE_CASE)
	)
	private val uniformRe = Regex(
		pattern = """
        ^\s*uniform\s+
        ([A-Za-z][A-Za-z0-9]*(?:[234](?:x[234])?)?)   # type
        \s+([A-Za-z_]\w*)                              # name
        \s*;\s*
        (?:\/\/\s*previewtype\s*:\s*([A-Za-z_][A-Za-z0-9_\- ]*))?   # optional trailing preview
        \s*$
    """.trimIndent(),
		options = setOf(RegexOption.IGNORE_CASE, RegexOption.COMMENTS)
	)

	operator fun invoke(sourceAgsl: String): List<Uniform> {
		val result = mutableListOf<Uniform>()
		var pendingPreview: PreviewType? = null

		sourceAgsl.lineSequence().forEach { line ->
			previewLineRe.matchEntire(line)?.let { m ->
				pendingPreview = parsePreviewType(m.groupValues[1])
				return@forEach
			}

			uniformRe.matchEntire(line)?.let { m ->
				val typeToken = m.groupValues[1]
				val name = m.groupValues[2]
				val inlinePreview = m.groupValues.getOrNull(3)?.takeIf { it.isNotBlank() }
				val preview = inlinePreview?.let(::parsePreviewType) ?: pendingPreview

				val type = mapUniformType(typeToken)
				if (type != null)
					result += Uniform(name = name, type = type, previewType = preview)

				pendingPreview = null
				return@forEach
			}
		}
		return result
	}

	private fun parsePreviewType(s: String?): PreviewType? = when (s?.trim()?.lowercase()) {
		null, "" -> null
		"number" -> PreviewType.Number
		"color" -> PreviewType.Color
		"size" -> PreviewType.Size
		"time" -> PreviewType.Time
		else -> null
	}

	private fun mapUniformType(raw: String): UniformType? {
		return when (raw.lowercase()) {
			"float", "half" -> UniformType.Float1
			"float2", "half2", "vec2" -> UniformType.Float2
			"float3", "half3", "vec3" -> UniformType.Float3
			"float4", "half4", "vec4" -> UniformType.Float4
			"bool" -> UniformType.Bool1
			"bool2", "bvec2" -> UniformType.Bool2
			"bool3", "bvec4" -> UniformType.Bool3
			"bool4", "bvec5" -> UniformType.Bool4
			"int", "short" -> UniformType.Int1
			"int2", "ivec2", "short2" -> UniformType.Int2
			"int3", "ivec3", "short3" -> UniformType.Int3
			"int4", "ivec4", "short4" -> UniformType.Int4
			"mat2", "float2x2", "half2x2" -> UniformType.Mat2
			"mat3", "float3x3", "half3x3" -> UniformType.Mat3
			"mat4", "float4x4", "half4x4" -> UniformType.Mat4
			"shader" -> UniformType.Shader
			else -> null
		}
	}
}