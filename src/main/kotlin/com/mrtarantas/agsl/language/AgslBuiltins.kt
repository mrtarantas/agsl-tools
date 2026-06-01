package com.mrtarantas.agsl.language

object AgslBuiltins {
	val FUNCTIONS = listOf(
		"radians", "degrees", "sin", "cos", "tan", "asin", "acos", "atan",
		"pow", "exp", "log", "exp2", "log2", "sqrt", "inversesqrt",
		"abs", "sign", "floor", "ceil", "fract", "mod",
		"min", "max", "mix", "clamp", "saturate", "step", "smoothstep",
		"length", "distance", "dot", "cross", "normalize", "faceforward", "reflect", "refract",
		"matrixCompMult", "inverse",
		"unpremul", "toLinearSrgb", "fromLinearSrgb",
	)

	val METHODS = listOf("eval")

	val SWIZZLES = listOf("x", "y", "z", "w", "r", "g", "b", "a")

	private val FUNCTION_SET = FUNCTIONS.toHashSet()
	private val METHOD_SET = METHODS.toHashSet()

	fun isBuiltinFunction(name: String): Boolean = name in FUNCTION_SET
	fun isBuiltinMethod(name: String): Boolean = name in METHOD_SET

	private val DOCS: Map<String, String> = mapOf(
		"radians" to "radians(degrees) — converts degrees to radians.",
		"degrees" to "degrees(radians) — converts radians to degrees.",
		"sin" to "sin(angle) — sine of the angle, in radians.",
		"cos" to "cos(angle) — cosine of the angle, in radians.",
		"tan" to "tan(angle) — tangent of the angle, in radians.",
		"asin" to "asin(x) — arc sine; returns the angle in radians.",
		"acos" to "acos(x) — arc cosine; returns the angle in radians.",
		"atan" to "atan(y_over_x) or atan(y, x) — arc tangent; the two-argument form returns the angle of the vector (x, y).",
		"pow" to "pow(x, y) — x raised to the power y.",
		"exp" to "exp(x) — natural exponentiation, e^x.",
		"log" to "log(x) — natural logarithm, ln(x).",
		"exp2" to "exp2(x) — 2 raised to the power x.",
		"log2" to "log2(x) — base-2 logarithm of x.",
		"sqrt" to "sqrt(x) — square root of x.",
		"inversesqrt" to "inversesqrt(x) — inverse square root, 1 / sqrt(x).",
		"abs" to "abs(x) — absolute value, component-wise.",
		"sign" to "sign(x) — −1, 0 or 1 depending on the sign of x.",
		"floor" to "floor(x) — largest integer not greater than x.",
		"ceil" to "ceil(x) — smallest integer not less than x.",
		"fract" to "fract(x) — fractional part, x − floor(x).",
		"mod" to "mod(x, y) — modulo, x − y · floor(x / y).",
		"min" to "min(a, b) — smaller of the two values, component-wise.",
		"max" to "max(a, b) — larger of the two values, component-wise.",
		"mix" to "mix(x, y, a) — linear interpolation: x · (1 − a) + y · a.",
		"clamp" to "clamp(x, minVal, maxVal) — constrains x to the range [minVal, maxVal].",
		"saturate" to "saturate(x) — clamps x to the range [0, 1].",
		"step" to "step(edge, x) — returns 0 if x < edge, otherwise 1.",
		"smoothstep" to "smoothstep(edge0, edge1, x) — smooth Hermite interpolation between 0 and 1.",
		"length" to "length(v) — length (magnitude) of the vector v.",
		"distance" to "distance(p0, p1) — distance between the two points.",
		"dot" to "dot(a, b) — dot product of two vectors.",
		"cross" to "cross(a, b) — cross product of two 3-component vectors.",
		"normalize" to "normalize(v) — vector with the same direction as v and length 1.",
		"faceforward" to "faceforward(N, I, Nref) — orients N to point away from the surface.",
		"reflect" to "reflect(I, N) — reflection of incident vector I about the normal N.",
		"refract" to "refract(I, N, eta) — refraction of I through a surface with normal N and index ratio eta.",
		"matrixCompMult" to "matrixCompMult(a, b) — component-wise multiplication of two matrices.",
		"inverse" to "inverse(m) — inverse of the square matrix m.",
		"unpremul" to "unpremul(color) — un-premultiplies the color by its alpha.",
		"toLinearSrgb" to "toLinearSrgb(color) — converts a color from the working space to linear sRGB.",
		"fromLinearSrgb" to "fromLinearSrgb(color) — converts a color from linear sRGB to the working space.",
		"eval" to "shader.eval(coord) — samples the child shader at coord, returning a half4 color.",
	)

	fun docFor(name: String): String? = DOCS[name]
}
