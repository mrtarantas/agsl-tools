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

	private val Float = "float"
	private val Half = "half"
	private val Float3 = "float3"
	private val Half3 = "half3"
	private val Vec3 = listOf(Float3, Half3)
	private val GT = listOf(Float, "float2", Float3, "float4", Half, "half2", Half3, "half4")
	private val Matrix = listOf("float2x2", "float3x3", "float4x4", "half2x2", "half3x3", "half4x4")

	private fun List<String>.except(vararg types: String): List<String> {
		return filter { type -> types.none { exceptType -> exceptType == type } }
	}

	private fun expandParameters(names: List<String>, types: List<String>): List<List<String>> {
		return types.map { type ->
			names.map { name ->
				buildString {
					append(type)
					append(' ')
					append(name)
				}
			}
		}
	}

	private fun expandParametersPreMix(vararg static: Pair<String, String>, names: List<String>, types: List<String>): List<List<String>> {
		return types.map { type ->
			static.map { staticParam ->
				buildString {
					val (type, name) = staticParam
					append(type)
					append(' ')
					append(name)
				}
			} + names.map { name ->
				buildString {
					append(type)
					append(' ')
					append(name)
				}
			}
		}
	}

	private fun expandParametersPostMix(names: List<String>, types: List<String>, vararg static: Pair<String, String>): List<List<String>> {
		return types.map { type ->
			names.map { name ->
				buildString {
					append(type)
					append(' ')
					append(name)
				}
			} + static.map { staticParam ->
				buildString {
					val (type, name) = staticParam
					append(type)
					append(' ')
					append(name)
				}
			}
		}
	}

	private val SIGNATURES: Map<String, List<List<String>>> = mapOf(
		"radians" to expandParameters(listOf("degrees"), GT),
		"degrees" to expandParameters(listOf("radians"), GT),
		"sin" to expandParameters(listOf("angle"), GT),
		"cos" to expandParameters(listOf("angle"), GT),
		"tan" to expandParameters(listOf("angle"), GT),
		"asin" to expandParameters(listOf("x"), GT),
		"acos" to expandParameters(listOf("x"), GT),
		"atan" to expandParameters(listOf("y_over_x"), GT) + expandParameters(listOf("y", "x"), GT),
		"pow" to expandParameters(listOf("x", "y"), GT),
		"exp" to expandParameters(listOf("x"), GT),
		"log" to expandParameters(listOf("x"), GT),
		"exp2" to expandParameters(listOf("x"), GT),
		"log2" to expandParameters(listOf("x"), GT),
		"sqrt" to expandParameters(listOf("x"), GT),
		"inversesqrt" to expandParameters(listOf("x"), GT),
		"abs" to expandParameters(listOf("x"), GT),
		"sign" to expandParameters(listOf("x"), GT),
		"floor" to expandParameters(listOf("x"), GT),
		"ceil" to expandParameters(listOf("x"), GT),
		"fract" to expandParameters(listOf("x"), GT),
		"mod" to expandParameters(listOf("x", "y"), GT) + expandParametersPostMix(listOf("x"), GT.except(Float), Float to "y"),
		"min" to expandParameters(listOf("a", "b"), GT) + expandParametersPostMix(listOf("a"), GT.except(Float), Float to "b"),
		"max" to expandParameters(listOf("a", "b"), GT) + expandParametersPostMix(listOf("a"), GT.except(Float), Float to "b"),
		"mix" to expandParameters(listOf("x", "y", "a"), GT) + expandParametersPostMix(listOf("x", "y"), GT.except(Float), Float to "a"),
		"clamp" to expandParameters(listOf("x", "minVal", "maxVal"), GT) +
			expandParametersPostMix(listOf("x"), GT.except(Float), Float to "minVal", Float to "maxVal"),
		"saturate" to expandParameters(listOf("x"), GT),
		"step" to expandParameters(listOf("edge", "x"), GT) +
			expandParametersPreMix(Float to "edge", names = listOf("x"), types = GT.except(Float)),
		"smoothstep" to expandParameters(listOf("edge0", "edge1", "x"), GT) +
			expandParametersPreMix(Float to "edge0", Float to "edge1", names = listOf("x"), types = GT.except(Float)),
		"length" to expandParameters(listOf("v"), GT),
		"distance" to expandParameters(listOf("p0", "p1"), GT),
		"dot" to expandParameters(listOf("a", "b"), GT),
		"cross" to expandParameters(listOf("a", "b"), Vec3),
		"normalize" to expandParameters(listOf("v"), GT),
		"faceforward" to expandParameters(listOf("N", "I", "Nref"), GT),
		"reflect" to expandParameters(listOf("I", "N"), GT),
		"refract" to expandParametersPostMix(listOf("I", "N"), GT, Float to "eta") +
			expandParametersPostMix(listOf("I", "N"), GT, Half to "eta"),
		"matrixCompMult" to expandParameters(listOf("a", "b"), Matrix),
		"inverse" to expandParameters(listOf("m"), Matrix),
		"unpremul" to listOf(listOf("half4 color")),
		"toLinearSrgb" to listOf(listOf("half3 color")),
		"fromLinearSrgb" to listOf(listOf("half3 color")),
		"eval" to listOf(listOf("float2 coord")),
	)

	fun signatureFor(name: String): List<List<String>>? = SIGNATURES[name]
}
