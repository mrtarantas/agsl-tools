package com.mrtarantas.agsl.language

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
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

private val INSERT_PARENS = InsertHandler<LookupElement> { context, _ ->
	val editor = context.editor
	val document = context.document
	val tail = context.tailOffset
	val hasParen = tail < document.textLength && document.charsSequence[tail] == '('
	if (!hasParen) document.insertString(tail, "()")
	editor.caretModel.moveToOffset(tail + 1)
	context.commitDocument()
	AutoPopupController.getInstance(context.project).autoPopupParameterInfo(editor, null)
}

private fun functionLookup(name: String, typeText: String): LookupElement =
	LookupElementBuilder.create(name)
		.withTypeText(typeText, true)
		.withTailText("()", true)
		.withInsertHandler(INSERT_PARENS)

class AgslCompletionContributor : CompletionContributor() {
	init {
		extend(CompletionType.BASIC, psiElement(), object : CompletionProvider<CompletionParameters>() {
			override fun addCompletions(
				params: CompletionParameters,
				ctx: ProcessingContext,
				result: CompletionResultSet,
			) {
				KEYWORDS.forEach { result.addElement(LookupElementBuilder.create(it)) }
				TYPES.forEach { result.addElement(LookupElementBuilder.create(it)) }
				(AgslBuiltins.FUNCTIONS + AgslBuiltins.METHODS).forEach {
					result.addElement(functionLookup(it, "built-in"))
				}
				AgslBuiltins.SWIZZLES.forEach { result.addElement(LookupElementBuilder.create(it)) }
				addFileFunctions(params, result)
			}
		})

		extend(CompletionType.BASIC, psiElement(AgslTypes.IDENT), object : CompletionProvider<CompletionParameters>() {
			override fun addCompletions(params: CompletionParameters, ctx: ProcessingContext, result: CompletionResultSet) {
				addFileFunctions(params, result)
			}
		})
	}

	private fun addFileFunctions(params: CompletionParameters, result: CompletionResultSet) {
		val file = params.originalFile as? AgslFile ?: return
		file.allFuncDefs().forEach { def ->
			def.name?.let { result.addElement(functionLookup(it, "func")) }
		}
	}
}
