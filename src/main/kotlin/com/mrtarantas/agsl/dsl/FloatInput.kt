package com.mrtarantas.agsl.dsl

import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.columns

private const val NUMBER_COLUMNS = 6

fun Row.floatInput(columns: Int = NUMBER_COLUMNS): Cell<JBTextField> {
	return textField().columns(columns).validationOnInput {
		val v = it.text.toFloatOrNull()
		if (v == null)
			error("Enter float number")
		else
			null
	}
}

fun Row.intInput(columns: Int = NUMBER_COLUMNS): Cell<JBTextField> {
	return textField().columns(columns).validationOnInput {
		val v = it.text.toIntOrNull()
		if (v == null)
			error("Enter int number")
		else
			null
	}
}
