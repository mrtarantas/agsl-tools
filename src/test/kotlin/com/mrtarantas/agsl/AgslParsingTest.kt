package com.mrtarantas.agsl

import com.intellij.psi.impl.DebugUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.ParsingTestCase
import com.mrtarantas.agsl.language.AgslParserDefinition
import com.mrtarantas.agsl.language.generated.psi.AgslFuncDef

class AgslParsingTest : ParsingTestCase("", "agsl", AgslParserDefinition()) {
	override fun getTestDataPath(): String = "src/test/testData"

	private fun funcs(code: String): Int {
		val file = createPsiFile("a", code)
		val all = PsiTreeUtil.findChildrenOfType(file, AgslFuncDef::class.java)
		if (all.isEmpty()) println(DebugUtil.psiToString(file, true))
		return all.size
	}

	fun testPlainFunction() {
		assertEquals(1, funcs("float foo(float x) { return x; }"))
	}

	fun testTheUsersFunction() {
		val code = """
			float bilinearCorner(float4 values, float2 uv) {
			    float top = mix(values.x, values.y, uv.x);
			    float bottom = mix(values.w, values.z, uv.x);
			    return mix(top, bottom, uv.y);
			}
		""".trimIndent()
		assertEquals(1, funcs(code))
	}

	fun testFunctionAfterUniform() {
		val code = """
			uniform shader background;
			uniform float2 iResolution;
			float foo(float x) { return x; }
		""".trimIndent()
		assertEquals(1, funcs(code))
	}

	fun testFunctionAfterBrokenLine() {
		val code = """
			uniform half iResolution
			float foo(float x) { return x; }
		""".trimIndent()
		assertTrue("function after a broken line did not parse", funcs(code) >= 1)
	}

	fun testBrokenUniformBetweenFunctions() {
		val code = """
			float a(float x) { return x; }
			uniform half bad
			float b(float y) { return y; }
		""".trimIndent()
		assertEquals(2, funcs(code))
	}

	fun testFunctionAfterBrokenVarDecl() {
		val code = """
			float x
			float foo(float a) { return a; }
		""".trimIndent()
		assertTrue("function after a broken var decl did not parse", funcs(code) >= 1)
	}

	fun testLocalsVisibleAtBrokenLineInBody() {
		val code = """
			float f(float4 values, float2 uv) {
			    float top = uv.x;
			    float bottom = uv.y;
			    bott
			    return top;
			}
		""".trimIndent()
		val offset = code.indexOf("bott\n") + 2
		val file = createPsiFile("a", code)
		val func = PsiTreeUtil.findChildrenOfType(file, AgslFuncDef::class.java)
			.firstOrNull { it.textRange.contains(offset) }
		assertNotNull("no enclosing function at the caret", func)
		val locals = PsiTreeUtil.findChildrenOfType(func!!, com.mrtarantas.agsl.language.generated.psi.AgslInitDeclarator::class.java)
			.map { it.name }
		val params = func.paramList?.paramList?.map { it.name } ?: emptyList()
		assertTrue("local 'top' not visible: $locals", "top" in locals)
		assertTrue("param 'values' not visible: $params", "values" in params)
	}

	fun testHalfTypedIdentifierLine() {
		val code = """
			float foo(float a) { return a; }
			somethin
			uniform float2 r;
		""".trimIndent()
		assertTrue("function before a half-typed line did not parse", funcs(code) >= 1)
	}
}
