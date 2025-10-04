package com.mrtarantas.agsl.language

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import com.mrtarantas.agsl.language.generated.psi.AgslTypes
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as DLH

class AgslSyntaxHighlighter : SyntaxHighlighterBase() {
	override fun getHighlightingLexer(): Lexer = AgslLexerAdapter()

	override fun getTokenHighlights(t: IElementType): Array<TextAttributesKey> = when (t) {
		AgslTypes.KW_UNIFORM, AgslTypes.KW_CONST, AgslTypes.KW_IF, AgslTypes.KW_FOR,
		AgslTypes.KW_WHILE, AgslTypes.KW_RETURN, AgslTypes.KW_SHADER ->
			pack(KEYWORD)

		AgslTypes.KW_MAT2, AgslTypes.KW_MAT3, AgslTypes.KW_MAT4,
		AgslTypes.KW_FLOAT2X2, AgslTypes.KW_FLOAT3X3, AgslTypes.KW_FLOAT4X4,
		AgslTypes.KW_HALF2X2, AgslTypes.KW_HALF3X3, AgslTypes.KW_HALF4X4,
		AgslTypes.KW_VEC2, AgslTypes.KW_VEC3, AgslTypes.KW_VEC4,
		AgslTypes.KW_BVEC2, AgslTypes.KW_BVEC3, AgslTypes.KW_BVEC4,
		AgslTypes.KW_IVEC2, AgslTypes.KW_IVEC3, AgslTypes.KW_IVEC4,
		AgslTypes.KW_HALF, AgslTypes.KW_HALF2, AgslTypes.KW_HALF3, AgslTypes.KW_HALF4,
		AgslTypes.KW_FLOAT, AgslTypes.KW_FLOAT2, AgslTypes.KW_FLOAT3, AgslTypes.KW_FLOAT4,
		AgslTypes.KW_BOOL, AgslTypes.KW_BOOL2, AgslTypes.KW_BOOL3, AgslTypes.KW_BOOL4,
		AgslTypes.KW_INT, AgslTypes.KW_INT2, AgslTypes.KW_INT3, AgslTypes.KW_INT4,
		AgslTypes.KW_SHORT, AgslTypes.KW_SHORT2, AgslTypes.KW_SHORT3, AgslTypes.KW_SHORT4 ->
			pack(TYPE)

		AgslTypes.IDENT -> pack(IDENT)
		AgslTypes.INT_LITERAL -> pack(NUMBER)
		AgslTypes.FLOAT_LITERAL -> pack(NUMBER)

		AgslTypes.LINE_COMMENT, AgslTypes.BLOCK_COMMENT -> pack(COMMENT)

		AgslTypes.EQ, AgslTypes.PLUS, AgslTypes.MINUS, AgslTypes.STAR, AgslTypes.SLASH,
		AgslTypes.EQEQ, AgslTypes.NEQ, AgslTypes.LT, AgslTypes.GT, AgslTypes.LE, AgslTypes.GE ->
			pack(OP)

		else -> emptyArray()
	}

	companion object {
		val KEYWORD = TextAttributesKey.createTextAttributesKey("AGSL_KEYWORD", DLH.KEYWORD)
		val TYPE = TextAttributesKey.createTextAttributesKey("AGSL_TYPE", DLH.KEYWORD)
		val IDENT = TextAttributesKey.createTextAttributesKey("AGSL_IDENT", DLH.IDENTIFIER)
		val NUMBER = TextAttributesKey.createTextAttributesKey("AGSL_NUMBER", DLH.NUMBER)
		val COMMENT = TextAttributesKey.createTextAttributesKey("AGSL_COMMENT", DLH.LINE_COMMENT)
		val OP = TextAttributesKey.createTextAttributesKey("AGSL_OPERATOR", DLH.OPERATION_SIGN)
	}
}