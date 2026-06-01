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
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.CollapsibleRow
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.components.validationTooltip
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.update.MergingUpdateQueue
import com.intellij.util.ui.update.Update
import com.mrtarantas.agsl.converters.AgslToSkiaShaderConverter
import com.mrtarantas.agsl.dsl.uniformRow
import com.mrtarantas.agsl.language.AgslFile
import com.mrtarantas.agsl.language.changeUniformPreviewType
import com.mrtarantas.agsl.mappers.UniformToUiStateMapper
import com.mrtarantas.agsl.parsers.UniformParser
import com.mrtarantas.agsl.uistates.UniformPropertyUiState
import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.LayoutManager
import java.beans.PropertyChangeListener
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingUtilities

class AgslPreviewEditor(
	private val project: Project,
	private val file: VirtualFile,
) : UserDataHolderBase(), FileEditor, DumbAware, Disposable {

	private val skiaPanel = AgslSkiaPanel()
	private val panel: JPanel = JPanel()
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
		val itemsPanel = ItemsPanel(model, ::buildUniformsPanel)
		var collapsingRow: CollapsibleRow? = null
		val uniformsPanel: DialogPanel = panel {
			collapsingRow = collapsibleGroup("Uniforms") {
				row {
					cell(itemsPanel).align(Align.FILL)
				}.resizableRow().topGap(TopGap.SMALL)
			}.apply {
				expanded = true
				resizableRow()
			}
		}
		val previewPanel: DialogPanel = panel {
			group("Preview") {
				row { cell(skiaPanel).align(Align.FILL).resizableColumn() }.resizableRow().topGap(TopGap.SMALL)
				row {
					validationTooltip(errorMessage).visibleIf(hasErrorMessage)
				}
			}.resizableRow()
		}

		val sectionGap = JBUI.scale(16)

		panel.layout = object : LayoutManager {
			override fun addLayoutComponent(name: String?, comp: Component?) {}
			override fun removeLayoutComponent(comp: Component?) {}

			override fun preferredLayoutSize(parent: Container): Dimension {
				val w = parent.width
				val h = parent.height
				val uniformsPref = if (collapsingRow?.expanded == true)
					uniformsPanel.preferredSize.height.coerceAtMost(if (h > 0) h / 2 else Int.MAX_VALUE)
				else
					uniformsPanel.preferredSize.height
				val previewPref = previewPanel.preferredSize.height
				return Dimension(w, uniformsPref + sectionGap + previewPref)
			}

			override fun minimumLayoutSize(parent: Container): Dimension = Dimension(0, 0)

			override fun layoutContainer(parent: Container) {
				val insets = parent.insets
				val x = insets.left
				val y = insets.top
				val w = parent.width - insets.left - insets.right
				val h = parent.height - insets.top - insets.bottom

				val uniformsH = if (collapsingRow?.expanded == true) {
					uniformsPanel.preferredSize.height.coerceAtMost(h / 2)
				} else {
					uniformsPanel.preferredSize.height
				}
				uniformsPanel.setBounds(x, y, w, uniformsH)
				val previewY = y + uniformsH + sectionGap
				previewPanel.setBounds(x, previewY, w, (h - uniformsH - sectionGap).coerceAtLeast(0))
			}
		}

		panel.add(uniformsPanel)
		panel.add(previewPanel)
		panel.border = JBUI.Borders.empty(8, 16)

		collapsingRow?.addExpandedListener { panel.revalidate() }

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

	private fun buildUniformsPanel(uniforms: List<UniformPropertyUiState>): JComponent {
		return panel {
			uniforms.forEachIndexed { index, property ->
				uniformRow(property, project, index < uniforms.lastIndex) { uniform, newType ->
					psiFile?.changeUniformPreviewType(uniform, newType)
				}
			}
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
						model.value = uniforms.map { uniform ->
							uniformToUiStateMapper.invoke(uniform, model.value.find { it.name == uniform.name }?.value)
						}
						skiaPanel.pushNewShader(skiaShader, model.value)
						panel.revalidate()
					}
				}
			}
		})
	}

	override fun getComponent(): JComponent = panel
	override fun getPreferredFocusedComponent(): JComponent = panel
	override fun getName(): String = "AGSL Preview"
	override fun setState(state: FileEditorState) {}
	override fun isModified(): Boolean = false
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
}
