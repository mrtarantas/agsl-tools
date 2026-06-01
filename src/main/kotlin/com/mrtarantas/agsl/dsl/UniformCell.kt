package com.mrtarantas.agsl.dsl

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.ui.ColorPanel
import com.intellij.ui.JBColor
import com.intellij.ui.components.ActionLink
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.*
import com.intellij.util.ui.JBUI
import com.mrtarantas.agsl.models.PreviewType
import com.mrtarantas.agsl.uistates.UniformPropertyUiState
import com.mrtarantas.agsl.uistates.UniformUiState
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider

fun Panel.uniformRow(
	property: UniformPropertyUiState,
	project: Project,
	hasSeparator: Boolean,
	onChangePreviewType: (uniform: String, newType: PreviewType) -> Unit,
) {
	row {
		cell(nameLabel(property.name)).align(AlignY.TOP)
		when (val uniform = property.value) {
			is UniformUiState.Float1 -> {
				floatInput().label("X ").text(uniform.value.toString())
					.onChanged {
						uniform.value = it.text.toFloatOrNull() ?: 0f
					}
				cell(JPanel()).resizableColumn()
				comboBox(listOf("Number", "Time")).onChanged {
					onChangePreviewType(property.name, if (it.selectedItem == "Time") PreviewType.Time else PreviewType.Number)
				}
			}
			is UniformUiState.Float2 -> {
				numberGrid(
					NumComp("X", uniform.x.toString()) { uniform.x = it.toFloatOrNull() ?: 0f },
					NumComp("Y", uniform.y.toString()) { uniform.y = it.toFloatOrNull() ?: 0f },
				)
				cell(JPanel()).resizableColumn()
				comboBox(listOf("Number", "Size")).onChanged {
					onChangePreviewType(property.name, if (it.selectedItem == "Size") PreviewType.Size else PreviewType.Number)
				}
			}
			is UniformUiState.Float3 -> {
				numberGrid(
					NumComp("X", uniform.x.toString()) { uniform.x = it.toFloatOrNull() ?: 0f },
					NumComp("Y", uniform.y.toString()) { uniform.y = it.toFloatOrNull() ?: 0f },
					NumComp("Z", uniform.z.toString()) { uniform.z = it.toFloatOrNull() ?: 0f },
				)
				cell(JPanel()).resizableColumn()
				comboBox(listOf("Number", "Color")).onChanged {
					onChangePreviewType(property.name, if (it.selectedItem == "Color") PreviewType.Color else PreviewType.Number)
				}
			}
			is UniformUiState.Float4 -> {
				numberGrid(
					NumComp("X", uniform.x.toString()) { uniform.x = it.toFloatOrNull() ?: 0f },
					NumComp("Y", uniform.y.toString()) { uniform.y = it.toFloatOrNull() ?: 0f },
					NumComp("Z", uniform.z.toString()) { uniform.z = it.toFloatOrNull() ?: 0f },
					NumComp("W", uniform.w.toString()) { uniform.w = it.toFloatOrNull() ?: 0f },
				)
				cell(JPanel()).resizableColumn()
				comboBox(listOf("Number", "Color")).onChanged {
					onChangePreviewType(property.name, if (it.selectedItem == "Color") PreviewType.Color else PreviewType.Number)
				}
			}
			is UniformUiState.Size -> {
				label("Size of canvas")
				cell(JPanel()).resizableColumn()
				comboBox(listOf("Size", "Number")).onChanged {
					onChangePreviewType(property.name, if (it.selectedItem == "Size") PreviewType.Size else PreviewType.Number)
				}
			}
			is UniformUiState.Time -> {
				slider(0, 100, 5, 25)
					.label("Time speed")
					.applyToComponent {
						this.value = (uniform.speed * 25f).toInt()
						this.addChangeListener {
							uniform.speed = (it.source as JSlider).value / 25f
						}
					}
				cell(JPanel()).resizableColumn()
				comboBox(listOf("Time", "Number")).onChanged {
					onChangePreviewType(property.name, if (it.selectedItem == "Time") PreviewType.Time else PreviewType.Number)
				}
			}
			is UniformUiState.Rgb -> {
				cell(ColorPanel().apply {
					setSupportTransparency(false)
					addActionListener {
						uniform.color = selectedColor ?: JBColor.WHITE
					}
				})
				cell(JPanel()).resizableColumn()
				comboBox(listOf("Color", "Number")).onChanged {
					onChangePreviewType(property.name, if (it.selectedItem == "Color") PreviewType.Color else PreviewType.Number)
				}
			}
			is UniformUiState.Argb -> {
				cell(ColorPanel().apply {
					setSupportTransparency(true)
					addActionListener {
						uniform.color = selectedColor ?: JBColor.WHITE
					}
				})
				cell(JPanel()).resizableColumn()
				comboBox(listOf("Color", "Number")).onChanged {
					onChangePreviewType(property.name, if (it.selectedItem == "Color") PreviewType.Color else PreviewType.Number)
				}
			}
			is UniformUiState.Shader -> {
				var link: ActionLink? = null
				val onActionClick = {
					val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
						.withFileFilter { it.extension?.lowercase() in setOf("png", "jpg", "jpeg") }
					FileChooser.chooseFile(descriptor, project, null)?.let { vf ->
						link?.text = vf.name
						uniform.imageFile = vf
					}
				}
				link(uniform.imageFile?.name ?: "Select picture...") {
					onActionClick.invoke()
				}.applyToComponent {
					link = this
				}
				actionButton(object : AnAction("Select Picture", "Open file selection dialog", AllIcons.Actions.MenuOpen) {
					override fun actionPerformed(e: AnActionEvent) {
						onActionClick.invoke()
					}
				})
			}
			is UniformUiState.Bool1 -> {
				checkBox("")
			}
			is UniformUiState.Bool2 -> {
				checkBox("")
				checkBox("")
			}
			is UniformUiState.Bool3 -> {
				checkBox("")
				checkBox("")
				checkBox("")
			}
			is UniformUiState.Bool4 -> {
				checkBox("")
				checkBox("")
				checkBox("")
				checkBox("")
			}
			is UniformUiState.Int1 -> {
				intInput().label("X ").text(uniform.value.toString())
					.onChanged {
						uniform.value = it.text.toIntOrNull() ?: 0
					}
			}
			is UniformUiState.Int2 -> {
				numberGrid(
					NumComp("X", uniform.x.toString()) { uniform.x = it.toIntOrNull() ?: 0 },
					NumComp("Y", uniform.y.toString()) { uniform.y = it.toIntOrNull() ?: 0 },
					isInt = true,
				)
			}
			is UniformUiState.Int3 -> {
				numberGrid(
					NumComp("X", uniform.x.toString()) { uniform.x = it.toIntOrNull() ?: 0 },
					NumComp("Y", uniform.y.toString()) { uniform.y = it.toIntOrNull() ?: 0 },
					NumComp("Z", uniform.z.toString()) { uniform.z = it.toIntOrNull() ?: 0 },
					isInt = true,
				)
			}
			is UniformUiState.Int4 -> {
				numberGrid(
					NumComp("X", uniform.x.toString()) { uniform.x = it.toIntOrNull() ?: 0 },
					NumComp("Y", uniform.y.toString()) { uniform.y = it.toIntOrNull() ?: 0 },
					NumComp("Z", uniform.z.toString()) { uniform.z = it.toIntOrNull() ?: 0 },
					NumComp("W", uniform.w.toString()) { uniform.w = it.toIntOrNull() ?: 0 },
					isInt = true,
				)
			}
			is UniformUiState.Mat2 -> {
				panel {
					row {
						floatInput().text(uniform.m00.toString())
							.onChanged {
								uniform.m00 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m01.toString())
							.onChanged {
								uniform.m01 = it.text.toFloatOrNull() ?: 0f
							}
					}
					row {
						floatInput().text(uniform.m10.toString())
							.onChanged {
								uniform.m10 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m11.toString())
							.onChanged {
								uniform.m11 = it.text.toFloatOrNull() ?: 0f
							}
					}
				}
			}
			is UniformUiState.Mat3 -> {
				panel {
					row {
						floatInput().text(uniform.m00.toString())
							.onChanged {
								uniform.m00 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m01.toString())
							.onChanged {
								uniform.m01 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m02.toString())
							.onChanged {
								uniform.m02 = it.text.toFloatOrNull() ?: 0f
							}
					}
					row {
						floatInput().text(uniform.m10.toString())
							.onChanged {
								uniform.m10 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m11.toString())
							.onChanged {
								uniform.m11 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m12.toString())
							.onChanged {
								uniform.m12 = it.text.toFloatOrNull() ?: 0f
							}
					}
					row {
						floatInput().text(uniform.m20.toString())
							.onChanged {
								uniform.m20 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m21.toString())
							.onChanged {
								uniform.m21 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m22.toString())
							.onChanged {
								uniform.m22 = it.text.toFloatOrNull() ?: 0f
							}
					}
				}
			}
			is UniformUiState.Mat4 -> {
				panel {
					row {
						floatInput().text(uniform.m00.toString())
							.onChanged {
								uniform.m00 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m01.toString())
							.onChanged {
								uniform.m01 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m02.toString())
							.onChanged {
								uniform.m02 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m03.toString())
							.onChanged {
								uniform.m03 = it.text.toFloatOrNull() ?: 0f
							}
					}
					row {
						floatInput().text(uniform.m10.toString())
							.onChanged {
								uniform.m10 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m11.toString())
							.onChanged {
								uniform.m11 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m12.toString())
							.onChanged {
								uniform.m12 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m13.toString())
							.onChanged {
								uniform.m13 = it.text.toFloatOrNull() ?: 0f
							}
					}
					row {
						floatInput().text(uniform.m20.toString())
							.onChanged {
								uniform.m20 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m21.toString())
							.onChanged {
								uniform.m21 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m22.toString())
							.onChanged {
								uniform.m22 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m23.toString())
							.onChanged {
								uniform.m23 = it.text.toFloatOrNull() ?: 0f
							}
					}
					row {
						floatInput().text(uniform.m30.toString())
							.onChanged {
								uniform.m30 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m31.toString())
							.onChanged {
								uniform.m31 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m32.toString())
							.onChanged {
								uniform.m32 = it.text.toFloatOrNull() ?: 0f
							}
						floatInput().text(uniform.m33.toString())
							.onChanged {
								uniform.m33 = it.text.toFloatOrNull() ?: 0f
							}
					}
				}
			}
		}
	}
	if (hasSeparator)
		separator()
}

private const val NAME_COLUMN_WIDTH = 150

private fun nameLabel(name: String): JLabel {
	val width = JBUI.scale(NAME_COLUMN_WIDTH)
	return JBLabel("<html><div style='width:${width}px'>$name:</div></html>").apply {
		preferredSize = preferredSize.apply { this.width = width }
	}
}

private class NumComp(val label: String, val initial: String, val onChange: (String) -> Unit)

private fun Row.numberGrid(vararg comps: NumComp, isInt: Boolean = false) {
	panel {
		comps.toList().chunked(2).forEach { chunk ->
			row {
				chunk.forEach { c ->
					val input = if (isInt) intInput() else floatInput()
					input.label("${c.label} ").text(c.initial).onChanged { c.onChange(it.text) }
				}
			}
		}
	}
}