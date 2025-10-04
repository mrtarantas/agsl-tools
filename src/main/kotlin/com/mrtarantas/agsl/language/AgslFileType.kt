package com.mrtarantas.agsl.language

import com.intellij.openapi.fileTypes.LanguageFileType
import com.mrtarantas.agsl.AgslIcons

object AgslFileType : LanguageFileType(AgslLanguage) {
	override fun getName() = "AndroidShaderLanguage"

	override fun getDescription() = "Android graphics shader language"

	override fun getDefaultExtension() = "agsl"

	override fun getIcon() = AgslIcons.File
}