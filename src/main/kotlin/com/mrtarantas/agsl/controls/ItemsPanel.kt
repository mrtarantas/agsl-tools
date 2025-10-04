package com.mrtarantas.agsl.controls

import com.intellij.openapi.Disposable
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.swing.Swing
import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.JComponent

class ItemsPanel<Item>(
	private val items: StateFlow<List<Item>>,
	private val cells: (index: Int, item: Item) -> JComponent,
) : JBPanel<ItemsPanel<Item>>(BorderLayout()), Disposable {

	private val container = JBPanel<JBPanel<*>>().apply {
		layout = BoxLayout(this, BoxLayout.Y_AXIS)
		isOpaque = false
	}
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

	init {
		add(JBScrollPane(container), BorderLayout.CENTER)
		scope.launch {
			items.collectLatest { list ->
				withContext(Dispatchers.Swing) { rebuild(list) }
			}
		}
	}

	fun rebuild(unwrapped: List<Item>) {
		container.removeAll()
		for (i in unwrapped.indices) {
			container.add(cells.invoke(i, unwrapped[i]))
		}
		container.revalidate()
		container.repaint()
	}

	override fun dispose() {
		scope.cancel()
	}
}