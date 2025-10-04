package com.mrtarantas.agsl.converters

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import com.mrtarantas.agsl.language.AgslFile
import com.mrtarantas.agsl.language.AgslLanguage
import com.mrtarantas.agsl.language.generated.psi.AgslEqualityExpr
import com.mrtarantas.agsl.language.generated.psi.AgslExpr
import com.mrtarantas.agsl.language.generated.psi.AgslUniformDecl
import com.mrtarantas.agsl.language.name
import com.mrtarantas.agsl.language.variableNames

class AgslToSkiaShaderConverter {
	operator fun invoke(originalText: String, project: Project): String {
		val psiFile = PsiFileFactory.getInstance(project)
			.createFileFromText("temp.agsl", AgslLanguage, originalText) as AgslFile
		val boolVars = mutableSetOf<String>()
		psiFile.accept(object : PsiRecursiveElementVisitor() {
			override fun visitElement(element: PsiElement) {
				when (element) {
					is AgslUniformDecl -> {
						when {
							element.type.text == "bool" -> {
								element.type.replace(createType("int", project))
								element.variableNames().forEach { boolVars += it }
							}
							element.type.text == "bool2" -> {
								element.type.replace(createType("int2", project))
								element.variableNames().forEach { boolVars += it }
							}
							element.type.text == "bool3" -> {
								element.type.replace(createType("int3", project))
								element.variableNames().forEach { boolVars += it }
							}
							element.type.text == "bool4" -> {
								element.type.replace(createType("int4", project))
								element.variableNames().forEach { boolVars += it }
							}
							element.type.text == "short" -> {
								element.type.replace(createType("int", project))
							}
							element.type.text == "short2" -> {
								element.type.replace(createType("int2", project))
							}
							element.type.text == "short3" -> {
								element.type.replace(createType("int3", project))
							}
							element.type.text == "short4" -> {
								element.type.replace(createType("int4", project))
							}
						}
					}
					is AgslEqualityExpr -> {
						if (element.name() in boolVars)
							element.replace(createExpression("(${element.text} == 1)", project))
					}
				}
				super.visitElement(element)
			}
		})
		return psiFile.text
	}

	fun createType(typeText: String, project: Project): PsiElement {
		val tmpFile = PsiFileFactory.getInstance(project)
			.createFileFromText("tmp.agsl", AgslLanguage, "uniform $typeText dummy;") as AgslFile
		return tmpFile.firstChild.firstChild.children[0]
	}

	fun createExpression(exprText: String, project: Project): PsiElement {
		val tmpFile = PsiFileFactory.getInstance(project)
			.createFileFromText("tmp.agsl", AgslLanguage, "void main() { $exprText; }") as AgslFile
		val block = tmpFile.firstChild
		return PsiTreeUtil.findChildOfType(block, AgslExpr::class.java)!!
	}
}

