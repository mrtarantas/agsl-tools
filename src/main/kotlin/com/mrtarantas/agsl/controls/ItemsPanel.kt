package com.mrtarantas.agsl.controls

import com.intellij.openapi.Disposable
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.swing.Swing
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.JComponent
import javax.swing.ScrollPaneConstants
import javax.swing.Scrollable

class ItemsPanel<Item>(
	private val items: StateFlow<List<Item>>,
	private val content: (List<Item>) -> JComponent,
) : JBPanel<ItemsPanel<Item>>(BorderLayout()), Disposable {

	private val scrollPane = JBScrollPane().apply {
		border = JBUI.Borders.empty()
		isOpaque = false
		viewport.isOpaque = false
		horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
	}
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

	init {
		isOpaque = false
		add(scrollPane, BorderLayout.CENTER)
		scope.launch {
			items.collectLatest { list ->
				withContext(Dispatchers.Swing) { rebuild(list) }
			}
		}
	}

	fun rebuild(list: List<Item>) {
		scrollPane.setViewportView(WidthTrackingWrapper(content.invoke(list)))
		scrollPane.revalidate()
		scrollPane.repaint()
		var p = parent
		while (p != null) {
			p.revalidate()
			p = p.parent
		}
	}

	override fun dispose() {
		scope.cancel()
	}

	private class WidthTrackingWrapper(view: JComponent) : JBPanel<WidthTrackingWrapper>(BorderLayout()), Scrollable {
		init {
			isOpaque = false
			add(view, BorderLayout.NORTH)
		}

		override fun getPreferredScrollableViewportSize(): Dimension = preferredSize
		override fun getScrollableUnitIncrement(visibleRect: Rectangle, orientation: Int, direction: Int): Int = 16
		override fun getScrollableBlockIncrement(visibleRect: Rectangle, orientation: Int, direction: Int): Int = 100
		override fun getScrollableTracksViewportWidth(): Boolean = true
		override fun getScrollableTracksViewportHeight(): Boolean = false
	}
}
