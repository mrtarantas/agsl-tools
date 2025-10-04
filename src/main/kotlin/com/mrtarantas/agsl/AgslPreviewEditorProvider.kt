package com.mrtarantas.agsl

import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.mrtarantas.agsl.controls.AgslPreviewEditor
import com.mrtarantas.agsl.language.AgslFileType

class AgslPreviewEditorProvider : FileEditorProvider, DumbAware {
	override fun accept(project: Project, file: VirtualFile): Boolean =
		file.fileType == AgslFileType

	override fun createEditor(project: Project, file: VirtualFile): FileEditor {
		val textEditor = TextEditorProvider.getInstance().createEditor(project, file) as TextEditor
		val previewEditor = AgslPreviewEditor(project, file)

		return TextEditorWithPreview(
			textEditor,
			previewEditor,
			"AGSL Preview",
			TextEditorWithPreview.Layout.SHOW_EDITOR_AND_PREVIEW,
		)
	}

	override fun getEditorTypeId(): String = "agsl-preview-editor"

	override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_DEFAULT_EDITOR
}