package com.mrtarantas.agsl.language.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.TokenType
import com.intellij.psi.util.PsiTreeUtil
import com.mrtarantas.agsl.language.AgslBuiltins
import com.mrtarantas.agsl.language.AgslFile
import com.mrtarantas.agsl.language.findFuncDefs
import com.mrtarantas.agsl.language.generated.psi.*

class AgslReference(element: PsiElement) :
	PsiReferenceBase<PsiElement>(element, TextRange(0, element.textLength)) {

	override fun resolve(): PsiElement? = AgslResolver.resolve(element)

	override fun handleElementRename(newElementName: String): PsiElement {
		val identifier = element.node.findChildByType(AgslTypes.IDENT)?.psi ?: return element
		identifier.replace(AgslElementFactory.createIdentifier(element.project, newElementName))
		return element
	}
}

class AgslFieldReference(element: PsiElement) :
	PsiReferenceBase<PsiElement>(element, TextRange(0, element.textLength)) {

	override fun resolve(): PsiElement = AgslBuiltinFunctionElement(element, element.text)
}

object AgslResolver {
	private val SCOPE_CLASSES = arrayOf(
		AgslCompoundStmt::class.java,
		AgslForStmt::class.java,
		AgslFuncDef::class.java,
		AgslFile::class.java,
	)

	fun resolve(refExpr: PsiElement): PsiElement? {
		val name = refExpr.node.findChildByType(AgslTypes.IDENT)?.text ?: return null
		return if (isCall(refExpr)) {
			resolveFunction(refExpr, name)
		} else {
			resolveVariable(refExpr, name) ?: resolveFunction(refExpr, name)
		}
	}

	private fun isCall(refExpr: PsiElement): Boolean {
		val primary = refExpr.parent as? AgslPrimaryExpr ?: return false
		if (primary.parent !is AgslPostfixExpr) return false
		var next = primary.node.treeNext
		while (next != null && next.elementType == TokenType.WHITE_SPACE) {
			next = next.treeNext
		}
		return next?.elementType == AgslTypes.LPAREN
	}

	private fun resolveFunction(context: PsiElement, name: String): PsiElement? {
		val file = context.containingFile as? AgslFile ?: return null
		file.findFuncDefs(name).firstOrNull()?.let { return it }
		if (AgslBuiltins.isBuiltinFunction(name)) return AgslBuiltinFunctionElement(context, name)
		return null
	}

	private fun nearestScope(element: PsiElement): PsiElement? =
		PsiTreeUtil.getParentOfType(element, *SCOPE_CLASSES)

	private fun resolveVariable(usage: PsiElement, name: String): PsiElement? {
		var scope = nearestScope(usage)
		while (scope != null) {
			if (scope is AgslFuncDef) {
				scope.paramList?.paramList?.firstOrNull { it.name == name }?.let { return it }
			}
			declaratorsDirectlyIn(scope)
				.filter { it.name == name && it.textOffset < usage.textOffset }
				.maxByOrNull { it.textOffset }
				?.let { return it }

			if (scope is AgslFile) {
				return resolveUniform(scope, name)
			}
			scope = nearestScope(scope)
		}
		return null
	}

	private fun declaratorsDirectlyIn(scope: PsiElement): List<AgslInitDeclarator> =
		PsiTreeUtil.findChildrenOfType(scope, AgslInitDeclarator::class.java)
			.filter { nearestScope(it) === scope }

	private fun resolveUniform(file: AgslFile, name: String): PsiElement? =
		PsiTreeUtil.findChildrenOfType(file, AgslUniformDecl::class.java)
			.flatMap { it.varList.varNameList }
			.firstOrNull { it.name == name }
}
