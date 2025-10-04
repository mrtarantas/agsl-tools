package com.mrtarantas.agsl.language

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import com.mrtarantas.agsl.language.generated.psi.AgslTypes

private val KEYWORDS = listOf(
	"uniform", "const", "shader",
	"if", "else", "for", "while", "do", "return", "break", "continue",
	"in", "out", "inout"
)
private val TYPES = listOf(
	"void", "bool", "int", "float", "short", "half",
	"vec2", "vec3", "vec4", "bvec2", "bvec3", "bvec4", "ivec2", "ivec3", "ivec4",
	"bool2", "bool3", "bool4", "int2", "int3", "int4",
	"float2", "float3", "float4", "half2", "half3", "half4", "short2", "short3", "short4",
	"mat2", "mat3", "mat4", "float2x2", "float3x3", "float4x4", "half2x2", "half3x3", "half4x4"
)
private val FUNCS = listOf(
	"radians", "degrees", "sin", "cos", "tan", "asin", "acos", "atan",
	"pow", "exp", "log", "exp2", "log2", "sqrt", "inversesqrt",
	"abs", "sign", "floor", "ceil", "fract", "mod", "min", "max", "clamp", "saturate", "step", "smoothstep",
	"length", "distance", "dot", "cross", "normalize", "faceforward", "reflect", "refract",
	"matrixCompMult", "inverse",
	"unpremul", "toLinearSrgb", "fromLinearSrgb",
	"eval",
	"x", "y", "z", "w",
	"r", "g", "b", "a"
)

class AgslCompletionContributor : CompletionContributor() {
	init {
		extend(CompletionType.BASIC, psiElement(), object : CompletionProvider<CompletionParameters>() {
			override fun addCompletions(
				params: CompletionParameters,
				ctx: ProcessingContext,
				result: CompletionResultSet
			) {
				// ключевые слова и типы
				KEYWORDS.forEach { result.addElement(LookupElementBuilder.create(it)) }
				TYPES.forEach { result.addElement(LookupElementBuilder.create(it)) }
				FUNCS.forEach { result.addElement(LookupElementBuilder.create(it)) }

				// имена функций из текущего файла
				val file = params.originalFile as? AgslFile ?: return
				file.findFuncDefs("").forEach { def ->
					def.nameIdentifier()?.text?.let { name ->
						result.addElement(LookupElementBuilder.create(name).withTypeText("func", true))
					}
				}
			}
		})

		extend(CompletionType.BASIC, psiElement(AgslTypes.IDENT), object : CompletionProvider<CompletionParameters>() {
			override fun addCompletions(params: CompletionParameters, ctx: ProcessingContext, result: CompletionResultSet) {
				val file = params.originalFile as? AgslFile ?: return
				file.findFuncDefs("").forEach { def ->
					def.nameIdentifier()?.text?.let { name ->
						result.addElement(LookupElementBuilder.create(name).withTypeText("func", true))
					}
				}
			}
		})
	}
}