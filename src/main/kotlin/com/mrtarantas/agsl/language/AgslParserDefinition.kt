package com.mrtarantas.agsl.language

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.mrtarantas.agsl.language.generated.parser.AgslParser
import com.mrtarantas.agsl.language.generated.psi.AgslTypes

class AgslParserDefinition : ParserDefinition {
	companion object {
		val FILE = IFileElementType(AgslLanguage)
		private val WS = TokenSet.create(TokenType.WHITE_SPACE)
		private val COMMENTS = TokenSet.create(AgslTypes.LINE_COMMENT, AgslTypes.BLOCK_COMMENT)
		private val STRINGS = TokenSet.EMPTY // строк пока нет
	}

	override fun createLexer(project: Project) = AgslLexerAdapter()
	override fun createParser(project: Project): PsiParser = AgslParser()

	override fun getFileNodeType(): IFileElementType = FILE
	override fun getWhitespaceTokens(): TokenSet = WS
	override fun getCommentTokens(): TokenSet = COMMENTS
	override fun getStringLiteralElements(): TokenSet = STRINGS

	override fun createElement(node: ASTNode): PsiElement = AgslTypes.Factory.createElement(node)
	override fun createFile(viewProvider: FileViewProvider): PsiFile = AgslFile(viewProvider)
}