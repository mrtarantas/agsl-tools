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
import com.intellij.ui.dsl.builder.actionButton
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.text
import com.mrtarantas.agsl.models.PreviewType
import com.mrtarantas.agsl.uistates.UniformPropertyUiState
import com.mrtarantas.agsl.uistates.UniformUiState
import java.awt.Dimension
import javax.swing.JPanel
import javax.swing.JSlider

fun uniformCell(
	property: UniformPropertyUiState,
	project: Project,
	hasSeparator: Boolean,
	onChangePreviewType: (uniform: String, newType: PreviewType) -> Unit,
) = panel {
	row {
		label("${property.name}: ").applyToComponent {
			minimumSize = Dimension(100, 0)
		}
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
				floatInput().label("X ").text(uniform.x.toString())
					.onChanged {
						uniform.x = it.text.toFloatOrNull() ?: 0f
					}
				floatInput().label("Y ").text(uniform.y.toString())
					.onChanged {
						uniform.y = it.text.toFloatOrNull() ?: 0f
					}
				cell(JPanel()).resizableColumn()
				comboBox(listOf("Number", "Size")).onChanged {
					onChangePreviewType(property.name, if (it.selectedItem == "Size") PreviewType.Size else PreviewType.Number)
				}
			}
			is UniformUiState.Float3 -> {
				floatInput().label("X ").text(uniform.x.toString())
					.onChanged {
						uniform.x = it.text.toFloatOrNull() ?: 0f
					}
				floatInput().label("Y ").text(uniform.y.toString())
					.onChanged {
						uniform.y = it.text.toFloatOrNull() ?: 0f
					}
				floatInput().label("Z ").text(uniform.z.toString())
					.onChanged {
						uniform.z = it.text.toFloatOrNull() ?: 0f
					}
				cell(JPanel()).resizableColumn()
				comboBox(listOf("Number", "Color")).onChanged {
					onChangePreviewType(property.name, if (it.selectedItem == "Color") PreviewType.Color else PreviewType.Number)
				}
			}
			is UniformUiState.Float4 -> {
				floatInput().label("X ").text(uniform.x.toString())
					.onChanged {
						uniform.x = it.text.toFloatOrNull() ?: 0f
					}
				floatInput().label("Y ").text(uniform.y.toString())
					.onChanged {
						uniform.y = it.text.toFloatOrNull() ?: 0f
					}
				floatInput().label("Z ").text(uniform.z.toString())
					.onChanged {
						uniform.z = it.text.toFloatOrNull() ?: 0f
					}
				floatInput().label("W ").text(uniform.w.toString())
					.onChanged {
						uniform.w = it.text.toFloatOrNull() ?: 0f
					}
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
				intInput().label("X ").text(uniform.x.toString())
					.onChanged {
						uniform.x = it.text.toIntOrNull() ?: 0
					}
				intInput().label("Y ").text(uniform.y.toString())
					.onChanged {
						uniform.y = it.text.toIntOrNull() ?: 0
					}
			}
			is UniformUiState.Int3 -> {
				intInput().label("X ").text(uniform.x.toString())
					.onChanged {
						uniform.x = it.text.toIntOrNull() ?: 0
					}
				intInput().label("Y ").text(uniform.y.toString())
					.onChanged {
						uniform.y = it.text.toIntOrNull() ?: 0
					}
				intInput().label("Z ").text(uniform.z.toString())
					.onChanged {
						uniform.z = it.text.toIntOrNull() ?: 0
					}
			}
			is UniformUiState.Int4 -> {
				intInput().label("X ").text(uniform.x.toString())
					.onChanged {
						uniform.x = it.text.toIntOrNull() ?: 0
					}
				intInput().label("Y ").text(uniform.y.toString())
					.onChanged {
						uniform.y = it.text.toIntOrNull() ?: 0
					}
				intInput().label("Z ").text(uniform.z.toString())
					.onChanged {
						uniform.z = it.text.toIntOrNull() ?: 0
					}
				intInput().label("W ").text(uniform.w.toString())
					.onChanged {
						uniform.w = it.text.toIntOrNull() ?: 0
					}
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