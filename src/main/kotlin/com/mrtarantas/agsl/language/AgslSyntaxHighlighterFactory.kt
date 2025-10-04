package com.mrtarantas.agsl.language

import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory
import com.intellij.openapi.fileTypes.SyntaxHighlighter

class AgslSyntaxHighlighterFactory : SingleLazyInstanceSyntaxHighlighterFactory() {
	override fun createHighlighter(): SyntaxHighlighter = AgslSyntaxHighlighter()
}