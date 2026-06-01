package com.mrtarantas.agsl.language

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.tree.TokenSet
import com.mrtarantas.agsl.language.generated.psi.*
import com.mrtarantas.agsl.language.psi.AgslNamedElement

class AgslFindUsagesProvider : FindUsagesProvider {
	override fun getWordsScanner(): WordsScanner = DefaultWordsScanner(
		AgslLexerAdapter(),
		TokenSet.create(AgslTypes.IDENT),
		TokenSet.create(AgslTypes.LINE_COMMENT, AgslTypes.BLOCK_COMMENT),
		TokenSet.create(AgslTypes.INT_LITERAL, AgslTypes.FLOAT_LITERAL),
	)

	override fun canFindUsagesFor(psiElement: PsiElement): Boolean = psiElement is AgslNamedElement

	override fun getHelpId(psiElement: PsiElement): String? = null

	override fun getType(element: PsiElement): String = when (element) {
		is AgslFuncDef -> "function"
		is AgslParam -> "parameter"
		is AgslVarName -> "uniform"
		is AgslInitDeclarator -> "variable"
		else -> "identifier"
	}

	override fun getDescriptiveName(element: PsiElement): String =
		(element as? PsiNamedElement)?.name ?: element.text

	override fun getNodeText(element: PsiElement, useFullName: Boolean): String =
		(element as? PsiNamedElement)?.name ?: element.text
}
