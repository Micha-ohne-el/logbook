package moe.micha.logbook.pretty

import kotlin.math.abs

/**
 * Set of three colors (red, green, and blue) which represent any color.
 *
 * @throws IllegalArgumentException when any of [red], [green], and [blue] is not in the range `0.0..1.0`.
 */
data class Color(
	val red: Double,
	val green: Double,
	val blue: Double,
) {
	val hue by lazy {
		if (isGray) return@lazy Double.NaN

		when (max) {
			red -> 1.0 / 6.0 * ((green - blue) / (max - min) % 6)
			green -> 1.0 / 6.0 * ((blue - red) / (max - min) + 2)
			blue -> 1.0 / 6.0 * ((red - green) / (max - min) + 4)
			else -> error("Unreachable.")
		}
	}

	val saturation by lazy {
		if (luminance == 0.0 || luminance == 1.0) {
			Double.NaN
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
		require(red in 0.0..1.0) { "All arguments must be in the range 0.0..1.0, but red isn't (value=$red)." }
		require(green in 0.0..1.0) { "All arguments must be in the range 0.0..1.0, but green isn't (value=$green)." }
		require(blue in 0.0..1.0) { "All arguments must be in the range 0.0..1.0, but blue isn't (value=$blue)." }
	}


	private val min by lazy { minOf(red, green, blue) }
	private val max by lazy { maxOf(red, green, blue) }

	private val isWhite get() = luminance == 1.0
	private val isBlack get() = luminance == 0.0
	private val isGray get() = isWhite || isBlack || saturation == 0.0

	companion object {
		/**
		 * Constructs a new [Color] from hue, saturation, and luminance.
		 *
		 * @param hue values outside the range `0.0..1.0` are wrapped (e.g. `1.5 -> 0.5` and `-1.25 -> 0.75`).
		 * @param saturation values outside the range `0.0..1.0` are clamped (e.g. `1.5 -> 1.0` and `-100 -> 0.0`).
		 * @param luminance values outside the range `0.0..1.0` are clamped (e.g. `1.5 -> 1.0` and `-100 -> 0.0`).
		 */
		// https://en.wikipedia.org/wiki/HSL_and_HSV#HSL_to_RGB_alternative
		fun fromHsl(hue: Double, saturation: Double, luminance: Double): Color {
			val h = hue wrapIn 0.0..1.0
			val s = saturation clampIn 0.0..1.0
			val l = luminance clampIn 0.0..1.0

			fun f(n: Double): Double {
				val a = s * minOf(l, 1.0 - l)
				val k = (n + h * 12.0) % 12.0

				return l - a * maxOf(-1.0, minOf(k - 3.0, 9.0 - k, 1.0))
			}

			return Color(f(0.0), f(8.0), f(4.0))
		}

		/**
		 * Constructs a new [Color] from an RGB triplet.
		 *
		 * ## Example usage:
		 * ```kt
		 * Color.fromRgb(0xFFFFFF) // full white
		 * Color.fromRgb(0xC4AFFE) // nice lavender color
		 * ```
		 *
		 * @throws IllegalArgumentException when value is outside the range `0x000000..0xFFFFFF`.
		 */
		fun fromRgb(value: Int): Color {
			require(value in 0x000000..0xFFFFFF) { "Value must be in the range 0x000000..0xFFFFFF (value=${value})." }

			val r = value and 0xFF0000 shr 16
			val g = value and 0x00FF00 shr 8
			val b = value and 0x0000FF shr 0

			return Color(r / 0xFF.toDouble(), g / 0xFF.toDouble(), b / 0xFF.toDouble())
		}

		val pureRed = Color(1.0, 0.0, 0.0)
		val pureYellow = Color(1.0, 1.0, 0.0)
		val pureGreen = Color(0.0, 1.0, 0.0)
		val pureCyan = Color(0.0, 1.0, 1.0)
		val pureBlue = Color(0.0, 0.0, 1.0)
		val pureMagenta = Color(1.0, 0.0, 1.0)
		val pureWhite = Color(1.0, 1.0, 1.0)
		val pureBlack = Color(0.0, 0.0, 0.0)


		private infix fun Double.clampIn(range: ClosedRange<Double>) = maxOf(range.start, minOf(this, range.endInclusive))

		private infix fun Double.wrapIn(range: ClosedRange<Double>) = this % (range.endInclusive - range.start) + range.start
	}
}
