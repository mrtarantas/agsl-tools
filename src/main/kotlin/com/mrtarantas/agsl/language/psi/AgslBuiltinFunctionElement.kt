package com.mrtarantas.agsl.language.psi

import com.intellij.codeInsight.hint.HintManager
import com.intellij.lang.Language
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.FakePsiElement
import com.mrtarantas.agsl.language.AgslLanguage

class AgslBuiltinFunctionElement(
	private val context: PsiElement,
	private val functionName: String,
) : FakePsiElement() {

	override fun getParent(): PsiElement = context

	override fun getContainingFile(): PsiFile? = context.containingFile

	override fun getLanguage(): Language = AgslLanguage

	override fun getName(): String = functionName

	override fun getPresentableText(): String = "$functionName — built-in AGSL"

	override fun canNavigate(): Boolean = true

	override fun canNavigateToSource(): Boolean = false

	override fun navigate(requestFocus: Boolean) {
		val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
		HintManager.getInstance().showInformationHint(editor, "'$functionName' is a built-in AGSL symbol — nowhere to jump")
	}
}
