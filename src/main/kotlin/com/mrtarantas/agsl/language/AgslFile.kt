package com.mrtarantas.agsl.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class AgslFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, AgslLanguage) {
	override fun getFileType(): FileType = AgslFileType
	override fun toString() = "AGSL File"
}