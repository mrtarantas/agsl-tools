package com.mrtarantas.agsl.controls

import com.intellij.openapi.vfs.VirtualFile
import com.mrtarantas.agsl.uistates.UniformPropertyUiState
import com.mrtarantas.agsl.uistates.UniformUiState
import com.mrtarantas.agsl.utils.tryClose
import org.jetbrains.skia.*
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoRenderDelegate
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.math.max

class AgslSkiaPanel : JPanel(BorderLayout()), SkikoRenderDelegate {
	private val layer = SkiaLayer()
	private var ticker: Timer? = null
	private var startNs = System.nanoTime()
	private var effect: RuntimeEffect? = null
	private val uniforms = mutableListOf<UniformPropertyUiState>()
	private val shaderCache = mutableMapOf<VirtualFile, ShaderCache>()
	var onShaderError: ((e: Throwable) -> Unit)? = null
	var onShaderSuccess: (() -> Unit)? = null

	private data class ShaderCache(val width: Int, val height: Int, val shader: Shader, val image: Image)

	init {
		layer.renderDelegate = this
		add(layer, BorderLayout.CENTER)
	}

	override fun addNotify() {
		super.addNotify()
		if (ticker == null) {
			ticker = Timer(16) { layer.needRedraw() }.also { it.start() }
		}
	}

	override fun removeNotify() {
		ticker?.stop()
		ticker = null
		super.removeNotify()
	}

	private fun releaseExpiredCache() {
		shaderCache.filter { entry -> uniforms.none { (it.value as? UniformUiState.Shader)?.imageFile == entry.key } }.forEach {
			shaderCache.remove(it.key)
			it.value.image.tryClose()
			it.value.shader.tryClose()
		}
	}

	override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
		releaseExpiredCache()
		canvas.clear(0xFF1E1F22.toInt())
		val t = (System.nanoTime() - startNs) / 1_000_000_000.0f
		try {
			val shader = effect?.let {
				val builder = RuntimeShaderBuilder(it)
				uniforms.forEach { uniform ->
					when (val value = uniform.value) {
						is UniformUiState.Float1 -> builder.uniform(uniform.name, value.value)
						is UniformUiState.Float2 -> builder.uniform(uniform.name, value.x, value.y)
						is UniformUiState.Float3 -> builder.uniform(uniform.name, value.x, value.y, value.z)
						is UniformUiState.Float4 -> builder.uniform(uniform.name, value.x, value.y, value.z, value.w)
						is UniformUiState.Bool1 -> builder.uniform(uniform.name, if (value.value) 1 else 0)
						is UniformUiState.Bool2 -> builder.uniform(uniform.name, if (value.x) 1 else 0, if (value.y) 1 else 0)
						is UniformUiState.Bool3 -> builder.uniform(uniform.name, if (value.x) 1 else 0, if (value.y) 1 else 0, if (value.z) 1 else 0)
						is UniformUiState.Bool4 -> builder.uniform(uniform.name, if (value.x) 1 else 0, if (value.y) 1 else 0, if (value.z) 1 else 0, if (value.w) 1 else 0)
						is UniformUiState.Int1 -> builder.uniform(uniform.name, value.value)
						is UniformUiState.Int2 -> builder.uniform(uniform.name, value.x, value.y)
						is UniformUiState.Int3 -> builder.uniform(uniform.name, value.x, value.y, value.z)
						is UniformUiState.Int4 -> builder.uniform(uniform.name, value.x, value.y, value.z, value.w)
						is UniformUiState.Mat2 -> builder.uniform(
							uniform.name, Matrix22(
								value.m00, value.m01,
								value.m10, value.m11,
							)
						)
						is UniformUiState.Mat3 -> builder.uniform(
							uniform.name, Matrix33(
								value.m00, value.m01, value.m02,
								value.m10, value.m11, value.m12,
								value.m20, value.m21, value.m22,
							)
						)
						is UniformUiState.Mat4 -> builder.uniform(
							uniform.name, Matrix44(
								value.m00, value.m01, value.m02, value.m03,
								value.m10, value.m11, value.m12, value.m13,
								value.m20, value.m21, value.m22, value.m23,
								value.m30, value.m31, value.m32, value.m33,
							)
						)
						is UniformUiState.Size -> builder.uniform(uniform.name, width.toFloat(), height.toFloat())
						is UniformUiState.Time -> builder.uniform(uniform.name, t * value.speed)
						is UniformUiState.Argb -> {
							builder.uniform(uniform.name, value.color.red / 255f, value.color.green / 255f, value.color.blue / 255f, value.color.alpha / 255f)
						}
						is UniformUiState.Rgb -> {
							builder.uniform(uniform.name, value.color.red / 255f, value.color.green / 255f, value.color.blue / 255f)
						}
						is UniformUiState.Shader -> {
							value.imageFile?.let { file ->
								shaderCache[file]?.let { cache ->
									if (cache.width != width || cache.height != height) {
										cache.shader.tryClose()
										shaderCache[file] = cache.copy(shader = cache.image.makePreviewShader(width, height))
									}
								}
								val cache = shaderCache[file] ?: run {
									val image = loadImage(file)
									val cache = ShaderCache(width, height, image.makePreviewShader(width, height), image)
									shaderCache[file] = cache
									cache
								}
								builder.child(uniform.name, cache.shader)
							}
						}
					}
				}
				builder.makeShader(null)
			}
			val paint = Paint().apply { this.shader = shader }
			canvas.drawRect(Rect(0f, 0f, width.toFloat(), height.toFloat()), paint)
		} catch (t: Throwable) {
			t.printStackTrace()
		}
	}

	fun pushNewShader(shader: String, uniforms: List<UniformPropertyUiState>) {
		try {
			effect = RuntimeEffect.makeForShader(shader)
			this.uniforms.clear()
			this.uniforms += uniforms
			onShaderSuccess?.invoke()
		} catch (t: Throwable) {
			onShaderError?.invoke(t)
			t.printStackTrace()
		}
	}

	fun dispose() {
		effect = null
		layer.dispose()
		shaderCache.values.forEach {
			it.shader.tryClose()
			it.image.tryClose()
		}
	}

	private fun Image.makePreviewShader(canvasWidth: Int, canvasHeight: Int): Shader {
		val sx = canvasWidth.toFloat() / width
		val sy = canvasHeight.toFloat() / height
		val maxScale = max(sx, sy)
		val localMatrix = Matrix33.makeScale(maxScale, maxScale)
		return makeShader(
			FilterTileMode.CLAMP,
			FilterTileMode.CLAMP,
			localMatrix,
		)
	}

	private fun loadImage(vf: VirtualFile): Image {
		val bytes = vf.contentsToByteArray()
		return Image.makeFromEncoded(bytes)
	}
}