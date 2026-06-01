package com.mrtarantas.agsl.language

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import com.mrtarantas.agsl.language.generated.psi.AgslPostfixExpr
import com.mrtarantas.agsl.language.generated.psi.AgslTypes
import com.mrtarantas.agsl.language.psi.AgslBuiltinMethodReference

class AgslReferenceContributor : PsiReferenceContributor() {
	override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
		registrar.registerReferenceProvider(
			PlatformPatterns.psiElement(AgslTypes.IDENT),
			object : PsiReferenceProvider() {
				override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
					if (!isBuiltinMethodUsage(element)) return PsiReference.EMPTY_ARRAY
					return arrayOf(AgslBuiltinMethodReference(element, element.text))
				}
			},
		)
	}

	private fun isBuiltinMethodUsage(element: PsiElement): Boolean {
		if (element.parent !is AgslPostfixExpr) return false
		if (!AgslBuiltins.isBuiltinMethod(element.text)) return false
		var prev = element.node.treePrev
		while (prev != null && prev.elementType == TokenType.WHITE_SPACE) {
			prev = prev.treePrev
		}
		return prev?.elementType == AgslTypes.DOT
	}
}
