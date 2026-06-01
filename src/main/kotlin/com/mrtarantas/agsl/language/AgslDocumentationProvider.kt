package com.mrtarantas.agsl.language

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.psi.PsiElement
import com.mrtarantas.agsl.language.psi.AgslBuiltinFunctionElement

class AgslDocumentationProvider : AbstractDocumentationProvider() {
	override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
		val name = (element as? AgslBuiltinFunctionElement)?.name ?: return null
		return "built-in AGSL: $name"
	}

	override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
		val name = (element as? AgslBuiltinFunctionElement)?.name ?: return null
		val doc = AgslBuiltins.docFor(name) ?: return null
		return buildString {
			append(DocumentationMarkup.DEFINITION_START)
			append("built-in AGSL · ").append(name)
			append(DocumentationMarkup.DEFINITION_END)
			append(DocumentationMarkup.CONTENT_START)
			append(doc)
			append(DocumentationMarkup.CONTENT_END)
		}
	}
}
