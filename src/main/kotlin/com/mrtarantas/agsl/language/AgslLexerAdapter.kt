package com.mrtarantas.agsl.language

import com.intellij.lexer.FlexAdapter
import com.mrtarantas.agsl.language.generated.lexer._AgslLexer

class AgslLexerAdapter : FlexAdapter(_AgslLexer(null))