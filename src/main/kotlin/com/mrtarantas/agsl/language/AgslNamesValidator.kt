package com.mrtarantas.agsl.language

import com.intellij.lang.refactoring.NamesValidator
import com.intellij.openapi.project.Project

class AgslNamesValidator : NamesValidator {
	override fun isKeyword(name: String, project: Project?): Boolean = name in KEYWORDS

	override fun isIdentifier(name: String, project: Project?): Boolean =
		IDENTIFIER.matches(name) && name !in KEYWORDS

	private companion object {
		val IDENTIFIER = Regex("[A-Za-z_][A-Za-z0-9_]*")
		val KEYWORDS = setOf(
			"void", "bool", "int", "float", "short", "half", "shader",
			"bvec2", "bvec3", "bvec4", "bool2", "bool3", "bool4",
			"ivec2", "ivec3", "ivec4", "int2", "int3", "int4",
			"vec2", "vec3", "vec4", "float2", "float3", "float4",
			"short2", "short3", "short4", "half2", "half3", "half4",
			"mat2", "mat3", "mat4",
			"float2x2", "float3x3", "float4x4", "half2x2", "half3x3", "half4x4",
			"uniform", "const", "in", "out", "inout",
			"if", "else", "for", "while", "do", "return", "break", "continue",
			"true", "false",
		)
	}
}
