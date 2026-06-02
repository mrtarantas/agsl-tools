package com.mrtarantas.agsl.language.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.mrtarantas.agsl.language.AgslBuiltins
import com.mrtarantas.agsl.language.generated.psi.AgslTypes

abstract class AgslNamedElementMixin(node: ASTNode) : ASTWrapperPsiElement(node), AgslNamedElement {
	override fun getNameIdentifier(): PsiElement? = node.findChildByType(AgslTypes.IDENT)?.psi

	override fun getName(): String? = nameIdentifier?.text

	override fun setName(name: String): PsiElement {
		nameIdentifier?.replace(AgslElementFactory.createIdentifier(project, name))
		return this
	}

	override fun getTextOffset(): Int = nameIdentifier?.textOffset ?: super.getTextOffset()
}

abstract class AgslReferenceExprMixin(node: ASTNode) : ASTWrapperPsiElement(node) {
	override fun getReference(): PsiReference = AgslReference(this)
}

abstract class AgslFieldAccessMixin(node: ASTNode) : ASTWrapperPsiElement(node) {
	override fun getReference(): PsiReference? =
		if (AgslBuiltins.isBuiltinMethod(text)) AgslFieldReference(this) else null
}
