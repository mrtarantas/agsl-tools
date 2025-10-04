package com.mrtarantas.agsl.language

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class AgslElementType @JvmOverloads constructor(debugName: @NonNls String, register: Boolean = true) : IElementType(debugName, AgslLanguage, register)