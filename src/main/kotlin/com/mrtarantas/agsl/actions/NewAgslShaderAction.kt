package com.mrtarantas.agsl.actions

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.mrtarantas.agsl.AgslIcons

private const val TEMPLATE_NAME = "AGSL Shader"

class NewAgslShaderAction : CreateFileFromTemplateAction(
	"AGSL Shader",
	"Creates AGSL shader file",
	AgslIcons.File,
) {

	override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
		builder
			.setTitle("New AGSL Shader")
			.addKind(TEMPLATE_NAME, AgslIcons.File, TEMPLATE_NAME)
	}

	override fun getActionName(directory: PsiDirectory, newName: String, templateName: String): String {
		return "Create AGSL Shader '$newName'"
	}
}
