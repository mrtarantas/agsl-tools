package com.mrtarantas.agsl.language

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import com.mrtarantas.agsl.language.psi.AgslNamedElement

class AgslRefactoringSupportProvider : RefactoringSupportProvider() {
	override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean =
		element is AgslNamedElement

	override fun isInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean =
		element is AgslNamedElement
}
