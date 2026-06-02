package com.mrtarantas.agsl.language

import com.intellij.lang.ASTNode
import com.intellij.lang.parameterInfo.*
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.util.PsiTreeUtil
import com.mrtarantas.agsl.language.generated.psi.*

class AgslParameterInfoHandler : ParameterInfoHandler<AgslPostfixExpr, AgslParameterInfoHandler.Signature> {
	class Signature(val params: List<String>)

	override fun findElementForParameterInfo(context: CreateParameterInfoContext): AgslPostfixExpr? {
		val postfix = postfixAt(context.file.findElementAt(context.offset), context.offset) ?: return null
		val name = calleeName(postfix, context.offset) ?: return null
		val signatures = signaturesFor(context.file, name)
		if (signatures.isEmpty()) return null
		context.itemsToShow = signatures.toTypedArray()
		return postfix
	}

	override fun showParameterInfo(element: AgslPostfixExpr, context: CreateParameterInfoContext) {
		context.showHint(element, element.textRange.startOffset, this)
	}

	override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): AgslPostfixExpr? =
		postfixAt(context.file.findElementAt(context.offset), context.offset)

	override fun updateParameterInfo(parameterOwner: AgslPostfixExpr, context: UpdateParameterInfoContext) {
		val argList = enclosingArgList(parameterOwner, context.offset)
		val index = when {
			argList != null -> ParameterInfoUtils.getCurrentParameterIndex(argList.node, context.offset, AgslTypes.COMMA)
			else -> 0
		}
		context.setCurrentParameter(index)
	}

	override fun updateUI(p: Signature, context: ParameterInfoUIContext) {
		if (p.params.isEmpty()) {
			context.setupUIComponentPresentation("<no parameters>", -1, -1, false, false, false, context.defaultParameterColor)
			return
		}

		val text = StringBuilder()
		var highlightStart = -1
		var highlightEnd = -1
		p.params.forEachIndexed { i, param ->
			if (i > 0) text.append(", ")
			val start = text.length
			text.append(param)
			if (i == context.currentParameterIndex) {
				highlightStart = start
				highlightEnd = text.length
			}
		}
		val disabled = context.currentParameterIndex >= p.params.size
		context.setupUIComponentPresentation(
			text.toString(), highlightStart, highlightEnd, disabled, false, false, context.defaultParameterColor,
		)
	}

	private fun postfixAt(element: PsiElement?, offset: Int): AgslPostfixExpr? {
		var postfix = PsiTreeUtil.getParentOfType(element, AgslPostfixExpr::class.java, false)
		while (postfix != null) {
			if (callLParen(postfix, offset) != null) return postfix
			postfix = PsiTreeUtil.getParentOfType(postfix, AgslPostfixExpr::class.java)
		}
		return null
	}

	private fun callLParen(postfix: AgslPostfixExpr, offset: Int): ASTNode? {
		var child = postfix.node.firstChildNode
		while (child != null) {
			if (child.elementType == AgslTypes.LPAREN) {
				val rparen = matchingRParen(child)
				val from = child.textRange.endOffset
				val to = rparen?.textRange?.startOffset ?: postfix.textRange.endOffset
				if (offset in from..to) return child
			}
			child = child.treeNext
		}
		return null
	}

	private fun matchingRParen(lparen: ASTNode): ASTNode? {
		var n = lparen.treeNext
		while (n != null) {
			if (n.elementType == AgslTypes.RPAREN) return n
			n = n.treeNext
		}
		return null
	}

	private fun enclosingArgList(postfix: AgslPostfixExpr, offset: Int): AgslArgumentList? {
		val lparen = callLParen(postfix, offset) ?: return null
		var n = lparen.treeNext
		while (n != null && n.elementType == TokenType.WHITE_SPACE) n = n.treeNext
		return n?.psi as? AgslArgumentList
	}

	private fun calleeName(postfix: AgslPostfixExpr, offset: Int): String? {
		val lparen = callLParen(postfix, offset) ?: return null
		var n = lparen.treePrev
		while (n != null && n.elementType == TokenType.WHITE_SPACE) n = n.treePrev
		return when (val callee = n?.psi) {
			is AgslPrimaryExpr -> callee.referenceExpr?.text
			is AgslFieldAccess -> callee.text
			else -> null
		}
	}

	private fun signaturesFor(file: PsiElement, name: String): List<Signature> {
		val agslFile = file as? AgslFile ?: return emptyList()
		val userDefs = agslFile.findFuncDefs(name)
		if (userDefs.isNotEmpty()) {
			return userDefs.map { def ->
				Signature(def.paramList?.paramList.orEmpty().map { paramLabel(it) })
			}
		}
		return AgslBuiltins.signatureFor(name)?.map { Signature(it) } ?: emptyList()
	}

	private fun paramLabel(param: AgslParam): String {
		val type = param.type.text
		val name = param.name
		return if (name != null) "$type $name" else type
	}
}
