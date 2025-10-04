package com.mrtarantas.agsl.controls

import com.intellij.openapi.Disposable
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.observable.properties.AtomicProperty
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.CollapsibleRow
import com.intellij.ui.dsl.builder.components.validationTooltip
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.update.MergingUpdateQueue
import com.intellij.util.ui.update.Update
import com.mrtarantas.agsl.converters.AgslToSkiaShaderConverter
import com.mrtarantas.agsl.dsl.uniformCell
import com.mrtarantas.agsl.language.AgslFile
import com.mrtarantas.agsl.language.changeUniformPreviewType
import com.mrtarantas.agsl.language.insertUniformsAfterLast
import com.mrtarantas.agsl.language.suggestUniqueName
import com.mrtarantas.agsl.mappers.UniformToUiStateMapper
import com.mrtarantas.agsl.parsers.UniformParser
import com.mrtarantas.agsl.uistates.UniformPropertyUiState
import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.Color
import java.awt.Container
import java.awt.Dimension
import java.beans.PropertyChangeListener
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.SwingUtilities
import kotlin.math.min

class AgslPreviewEditor(
	private val project: Project,
	private val file: VirtualFile,
) : UserDataHolderBase(), FileEditor, DumbAware, Disposable {

	private val skiaPanel = AgslSkiaPanel()
	private val panel: DialogPanel = DialogPanel()
	private val document: Document?
	private val psiFile: AgslFile?
	private val docListener: DocumentListener

	private val queue = MergingUpdateQueue(
		"agsl-preview", 150, true, panel, this, null, false
	)

	private val model = MutableStateFlow<List<UniformPropertyUiState>>(emptyList())
	private val uniformParser = UniformParser()
	private val uniformToUiStateMapper = UniformToUiStateMapper()
	private val agslToSkiaShaderConverter = AgslToSkiaShaderConverter()

	init {
		val errorMessage = AtomicProperty("")
		val hasErrorMessage = AtomicProperty(false)
		errorMessage.afterChange {
			hasErrorMessage.set(it.isNotBlank())
		}
		val itemsPanel = ItemsPanel(model, ::getUniformCell)
		panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
		var collapsingRow: CollapsibleRow? = null
		val uniforms = panel {
			collapsingRow = collapsibleGroup("Uniforms") {
				row {
					cell(itemsPanel).align(Align.FILL)
				}
			}.apply {
				expanded = true
			}
		}
		panel.add(uniforms)
		uniforms.layout = object : BoxLayout(uniforms, BoxLayout.Y_AXIS) {
			override fun maximumLayoutSize(target: Container?): Dimension {
				return Dimension(Int.MAX_VALUE, if (collapsingRow?.expanded == true) panel.height / 2 else -1)
			}

			override fun preferredLayoutSize(target: Container?): Dimension {
				val wrapSize = uniforms.components.sumOf { it.preferredSize.height }
				return Dimension(Int.MAX_VALUE, if (collapsingRow?.expanded == true) min(wrapSize, panel.height / 2) else 30)
			}
		}
		panel.add(panel {
			group("Preview") {
				row { cell(skiaPanel).align(Align.FILL).resizableColumn() }.resizableRow()
				row {
					validationTooltip(errorMessage).visibleIf(hasErrorMessage)
				}
			}.resizableRow()
		})
		panel.border = JBUI.Borders.customLine(transparent, 50)

		document = FileDocumentManager.getInstance().getDocument(file)
		docListener = object : DocumentListener {
			override fun documentChanged(event: DocumentEvent) = scheduleRebuild()
		}
		if (document != null) {
			psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) as? AgslFile
			document.addDocumentListener(docListener)
		} else {
			psiFile = null
		}
		skiaPanel.onShaderError = { errorMessage.set(it.message?.split('\n')?.firstOrNull() ?: "") }
		skiaPanel.onShaderSuccess = { errorMessage.set("") }
		scheduleRebuild()
	}

	private fun getUniformCell(index: Int, property: UniformPropertyUiState): JComponent {
		return uniformCell(property, project, index < model.value.indices.last) { uniform, newType ->
			psiFile?.changeUniformPreviewType(uniform, newType)
		}.apply {
			registerValidators(this@AgslPreviewEditor)
		}
	}

	private fun scheduleRebuild() {
		val src = document?.text.orEmpty()
		queue.queue(object : Update("render") {
			override fun run() {
				val uniforms = uniformParser.invoke(src)
				WriteCommandAction.runWriteCommandAction(project) {
					val skiaShader = agslToSkiaShaderConverter.invoke(src, project)
					SwingUtilities.invokeLater {
						model.value = uniforms.map { uniform -> uniformToUiStateMapper.invoke(uniform, model.value.find { it.name == uniform.name }?.value) }
						skiaPanel.pushNewShader(skiaShader, model.value)
					}
				}
			}
		})
	}

	private fun addNewUniform() {
		psiFile?.insertUniformsAfterLast(project, "uniform float4 ${psiFile.suggestUniqueName("name")}")
	}

	override fun getComponent(): JComponent = panel
	override fun getPreferredFocusedComponent(): JComponent = panel
	override fun getName(): String = "AGSL Preview"
	override fun setState(state: FileEditorState) {}
	override fun isModified(): Boolean = panel.isModified()
	override fun isValid(): Boolean = file.isValid

	override fun selectNotify() {}
	override fun deselectNotify() {}

	override fun addPropertyChangeListener(listener: PropertyChangeListener) {}
	override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

	override fun getCurrentLocation(): FileEditorLocation? = null

	override fun dispose() {
		document?.removeDocumentListener(docListener)
		skiaPanel.dispose()
	}

	companion object {
		private val transparent = JBColor(Color(0f, 0f, 0f, 0f), Color(0f, 0f, 0f, 0f))
	}
}