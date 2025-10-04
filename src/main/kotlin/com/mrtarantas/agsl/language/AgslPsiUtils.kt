package com.mrtarantas.agsl.language

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import com.mrtarantas.agsl.language.generated.psi.*
import com.mrtarantas.agsl.models.PreviewType
import com.mrtarantas.agsl.parsers.CommentPreviewTypeBuilder

fun AgslFuncDef.nameIdentifier(): PsiElement? =
	node.findChildByType(AgslTypes.IDENT)?.psi

fun AgslFile.findFuncDefs(name: String): Collection<AgslFuncDef> =
	PsiTreeUtil.findChildrenOfType(this, AgslFuncDef::class.java)
		.filter { it.nameIdentifier()?.text == name }

fun AgslUniformDecl.variableNames(): List<String> {
	return varList.text.split(',').map { it.trim() }
}

fun AgslFile.suggestUniqueName(origName: String): String {
	val allUniforms = PsiTreeUtil.findChildrenOfType(this, AgslUniformDecl::class.java)
	val allNames = allUniforms.flatMap { it.variableNames() }.toMutableList()
	var newName = origName
	var number = 2
	while (newName in allNames)
		newName = "${origName}${number++}"
	return newName
}

fun AgslFile.insertUniformsAfterLast(project: Project, declarationText: String) {
	ApplicationManager.getApplication().invokeLater {
		WriteCommandAction.runWriteCommandAction(project) {
			PsiDocumentManager.getInstance(project).commitAllDocuments()
			val text = declarationText.trim().let { if (it.endsWith(";")) it else "$it;" }
			val dummy = PsiFileFactory.getInstance(project)
				.createFileFromText("injected.agsl", AgslLanguage, text) as AgslFile
			val toInsert = PsiTreeUtil.findChildrenOfType(dummy, AgslUniformDecl::class.java).toList().mapNotNull { it.parent }
			if (toInsert.isEmpty()) return@runWriteCommandAction

			var anchor: PsiElement? = PsiTreeUtil.findChildrenOfType(this, AgslUniformDecl::class.java).lastOrNull()
			val parser = PsiParserFacade.getInstance(project)
			val codeStyle = CodeStyleManager.getInstance(project)
			for (declaration in toInsert) {
				val copy = declaration.copy()
				val inserted: PsiElement = if (anchor != null) {
					addAfter(copy, anchor.parent ?: anchor)
				} else {
					val first = firstChild
					if (first != null) addBefore(copy, first) else add(copy)
				}
				addBefore(parser.createWhiteSpaceFromText("\n"), inserted)
				codeStyle.reformat(inserted)
				anchor = inserted
			}
		}
	}
}

fun AgslUniformDecl.leadingComments(): List<PsiComment> {
	val declaration = this.parent as? AgslDeclaration ?: this
	val result = mutableListOf<PsiComment>()
	var e: PsiElement? = declaration.prevSibling
	while (e != null) {
		when (e) {
			is PsiWhiteSpace -> {
				if (e.text.count { it == '\n' } >= 2) break
			}
			is PsiComment -> {
				result.add(0, e)
			}
			else -> break
		}
		e = e.prevSibling
	}
	return result
}

fun AgslFile.changeUniformPreviewType(uniform: String, newType: PreviewType) {
	val declaration = PsiTreeUtil.findChildrenOfType(this, AgslUniformDecl::class.java).distinct().find { uniform in it.variableNames() }
	if (declaration != null) {
		WriteCommandAction.runWriteCommandAction(project) {
			val comments = declaration.leadingComments()
			val newPreviewTypeComment = CommentPreviewTypeBuilder().invoke(newType)
			val tmp = PsiFileFactory.getInstance(project).createFileFromText("dummy.agsl", AgslFileType, newPreviewTypeComment)
			val psi = addBefore(tmp.firstChild as PsiComment, declaration.parent ?: declaration)
			val parser = PsiParserFacade.getInstance(project)
			addAfter(parser.createWhiteSpaceFromText("\n"), psi)
			comments.forEach { it.delete() }
		}
	}
}

fun AgslExpr.name() = exprName()
fun AgslExprStmt.name() = exprName()
fun AgslEqualityExpr.name() = exprName()

private fun PsiElement.exprName(): String {
	return when {
		'[' in text -> text.split('[').first()
		'.' in text -> text.split('.').first()
		else -> text
	}
}