package com.mrtarantas.agsl.language

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.mrtarantas.agsl.language.generated.psi.*

class AgslAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		if (element is AgslUniformDecl) {
			element.children.filterIsInstance<AgslVarList>().forEach { varList ->
				varList.children.forEach { varName ->
					holder.highlightAsUniform(varName)
				}
			}
		} else {
			val resolved = element.reference?.resolve()
			if (resolved is AgslVarName && resolved.parent is AgslVarList && resolved.parent.parent is AgslUniformDecl)
				holder.highlightAsUniform(element)
		}
		if (element is AgslReferenceExpr) {
			val parent = element.parent
			if (parent is AgslPrimaryExpr && parent.textRange == element.textRange && element.text in AgslBuiltins.FUNCTIONS) {
				holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
					.range(parent)
					.textAttributes(AgslSyntaxHighlighter.BUILTIN)
					.create()
			}
		}
	}

	private fun AnnotationHolder.highlightAsUniform(element: PsiElement) {
		newSilentAnnotation(HighlightSeverity.INFORMATION)
			.range(element)
			.textAttributes(AgslSyntaxHighlighter.UNIFORM)
			.create()
	}
}

/*
* LeafPsiElement; clamp
AgslReferenceExprImpl; clamp
AgslPrimaryExprImpl; clamp
AgslPostfixExprImpl; clamp(1.0 - toCorner.x / max(cornerRadius, 0.001), 0.0, 1.0)
AgslPrefixExprImpl; clamp(1.0 - toCorner.x / max(cornerRadius, 0.001), 0.0, 1.0)
AgslMultiplicativeExprImpl; clamp(1.0 - toCorner.x / max(cornerRadius, 0.001), 0.0, 1.0)
AgslAdditiveExprImpl; clamp(1.0 - toCorner.x / max(cornerRadius, 0.001), 0.0, 1.0)
*
*
* LeafPsiElement; eval
AgslFieldAccessImpl; eval
AgslPostfixExprImpl; background.eval(coord)
AgslPrefixExprImpl; background.eval(coord)
AgslMultiplicativeExprImpl; background.eval(coord)
AgslAdditiveExprImpl; background.eval(coord)
AgslRelationalExprImpl; background.eval(coord)
AgslEqualityExprImpl; background.eval(coord)
AgslLogicalAndExprImpl; background.eval(coord)
* */