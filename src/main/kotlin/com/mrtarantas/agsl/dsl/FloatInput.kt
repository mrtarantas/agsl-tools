package com.mrtarantas.agsl.dsl

import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.columns

fun Row.floatInput(): Cell<JBTextField> {
	return textField().columns(10).validationOnInput {
		val v = it.text.toFloatOrNull()
		if (v == null)
			error("Enter float number")
		else
			null
	}
}

fun Row.intInput(): Cell<JBTextField> {
	return textField().columns(10).validationOnInput {
		val v = it.text.toIntOrNull()
		if (v == null)
			error("Enter int number")
		else
			null
	}
}