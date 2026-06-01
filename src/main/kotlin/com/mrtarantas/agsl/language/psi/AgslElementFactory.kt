package com.mrtarantas.agsl.language.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import com.mrtarantas.agsl.language.AgslFile
import com.mrtarantas.agsl.language.AgslLanguage
import com.mrtarantas.agsl.language.generated.psi.AgslInitDeclarator
import com.mrtarantas.agsl.language.generated.psi.AgslTypes

object AgslElementFactory {
	fun createFile(project: Project, text: String): AgslFile =
		PsiFileFactory.getInstance(project).createFileFromText("dummy.agsl", AgslLanguage, text) as AgslFile

	fun createIdentifier(project: Project, name: String): PsiElement {
		val file = createFile(project, "float $name;")
		val declarator = PsiTreeUtil.findChildOfType(file, AgslInitDeclarator::class.java)
			?: error("Cannot create AGSL identifier for '$name'")
		return declarator.node.findChildByType(AgslTypes.IDENT)!!.psi
	}
}
