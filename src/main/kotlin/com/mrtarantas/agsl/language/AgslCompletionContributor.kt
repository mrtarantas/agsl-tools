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
	if (!hasParen) {
		document.insertString(tail, "()")
		if (shouldAppendSemicolon(document.charsSequence, context.startOffset, tail + 2))
			document.insertString(tail + 2, ";")
	}
	editor.caretModel.moveToOffset(tail + 1)
	context.commitDocument()
	AutoPopupController.getInstance(context.project).autoPopupParameterInfo(editor, null)
}

private val STATEMENT_KEYWORDS = setOf("return", "else", "do")

private fun shouldAppendSemicolon(text: CharSequence, nameStart: Int, afterCloseParen: Int): Boolean {
	var f = afterCloseParen
	while (f < text.length && (text[f] == ' ' || text[f] == '\t')) f++
	if (f < text.length) {
		when (text[f]) {
			';', ')', ']', ',', '.', '?', ':', '+', '-', '*', '/', '<', '>', '=', '&', '|', '^' -> return false
		}
	}
	var b = nameStart - 1
	while (b >= 0 && text[b].isWhitespace()) b--
	if (b < 0) return true
	return when (text[b]) {
		';', '{', '}', ')' -> true
		else -> precedingWord(text, b) in STATEMENT_KEYWORDS
	}
}

private fun precedingWord(text: CharSequence, end: Int): String {
	var start = end
	while (start >= 0 && (text[start].isLetterOrDigit() || text[start] == '_')) start--
	return text.subSequence(start + 1, end + 1).toString()
}

private fun callLookup(name: String, params: List<String>, returnType: String): LookupElement =
	LookupElementBuilder.create(name)
		.withTailText("(" + params.joinToString(", ") + ")", true)
		.withTypeText(returnType, true)
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
				(AgslBuiltins.FUNCTIONS + AgslBuiltins.METHODS).forEach { name ->
					val overloads = AgslBuiltins.overloadsFor(name)
					if (overloads.isNullOrEmpty()) {
						result.addElement(callLookup(name, emptyList(), "built-in"))
					} else {
						overloads.forEach { o -> result.addElement(callLookup(name, o.params, o.returnType)) }
					}
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
			val name = def.name ?: return@forEach
			val paramLabels = def.paramList?.paramList.orEmpty().map { param ->
				param.name?.let { "${param.type.text} $it" } ?: param.type.text
			}
			result.addElement(callLookup(name, paramLabels, def.type.text))
		}
	}
}
