package moe.micha.logbook

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Color(
	val red: Double,
	val green: Double,
	val blue: Double,
) {
	val hue by lazy {
		when (max) {
			red -> 0.0 / 3.0 + (green - blue) / (max - min)
			green -> 1.0 / 3.0 + (blue - red) / (max - min)
			blue -> 2.0 / 3.0 + (red - green) / (max - min)
			else -> throw IllegalStateException("red, green, or blue was NaN, when it shouldn't be.")
		}
	}

	val saturation by lazy {
		if (luminance == 0.0 || luminance == 1.0) {
			0.0
		} else {
			(max - min) / (1.0 - abs(2.0 * luminance - 1.0))
		}
	}

	val luminance by lazy { (min + max) / 2 }

	val value by lazy { max }

	fun copyHsl(hue: Double? = null, saturation: Double? = null, luminance: Double? = null) = fromHsl(
		hue ?: this.hue,
		saturation ?: this.saturation,
		luminance ?: this.luminance,
	)


	init {
		if (!red.isFinite()) throw ArithmeticException("All arguments must be finite, but red isn't (value=$red).")
		if (!green.isFinite()) throw ArithmeticException("All arguments must be finite, but green isn't (value=$green).")
		if (!blue.isFinite()) throw ArithmeticException("All arguments must be finite, but blue isn't (value=$blue).")
	}


	private val min by lazy { minOf(red, green, blue) }
	private val max by lazy { maxOf(red, green, blue) }

	companion object {
		// https://en.wikipedia.org/wiki/HSL_and_HSV#HSL_to_RGB_alternative
		fun fromHsl(hue: Double, saturation: Double, luminance: Double): Color {
			val h = wrap(hue, 0.0, 1.0)
			val s = clamp(saturation, 0.0, 1.0)
			val l = clamp(luminance, 0.0, 1.0)

			fun f(n: Double): Double {
				val a = s * minOf(l, 1.0 - l)
				val k = (n + h * 12.0) % 12.0

				return l - a * maxOf(-1.0, minOf(k - 3.0, 9.0 - k, 1.0))
			}

			return Color(f(0.0), f(8.0), f(4.0))
		}

		val pureRed = Color(1.0, 0.0, 0.0)
		val pureYellow = Color(1.0, 1.0, 0.0)
		val pureGreen = Color(0.0, 1.0, 0.0)
		val pureCyan = Color(0.0, 1.0, 1.0)
		val pureBlue = Color(0.0, 0.0, 1.0)
		val pureMagenta = Color(1.0, 0.0, 1.0)
		val pureWhite = Color(1.0, 1.0, 1.0)
		val pureBlack = Color(0.0, 0.0, 0.0)


		private inline fun clamp(value: Double, minimum: Double, maximum: Double) = max(minimum, min(value, maximum))

		private inline fun wrap(value: Double, minimum: Double, maximum: Double) = value % (maximum - minimum) + minimum
	}
}
