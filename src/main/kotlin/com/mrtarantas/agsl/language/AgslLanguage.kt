package com.mrtarantas.agsl.language

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.lang.Language

object AgslLanguage : Language("AndroidShaderLanguage") {
	private fun readResolve(): Any = AgslLanguage
}