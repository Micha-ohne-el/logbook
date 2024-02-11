package moe.micha.logbook.outlets

import moe.micha.logbook.pretty.Color
import moe.micha.logbook.pretty.ColorInfo

/**
 * A very commonly used outlet that will color log messages correctly for most modern terminals.
 *
 * For info on how the colorization is achieved, see:
 * [Wikipedia: ANSI escape codes > Select Graphics Rendition](https://en.wikipedia.org/wiki/ANSI_escape_code#SGR)
 */
open class AnsiConsoleOutlet : ColoredConsoleOutlet() {
	override fun colorize(text: String, colorInfo: ColorInfo): String {
		if (colorInfo.foreground == null && colorInfo.background == null) {
			return text
		}

		val sets = setOfNotNull(getForegroundSet(colorInfo.foreground), getBackgroundSet(colorInfo.background))
		val resets = setOfNotNull(getForegroundReset(colorInfo.foreground), getBackgroundReset(colorInfo.background))

		return csi + sets.joinToString(";") + "m" +
			text +
			csi + resets.joinToString(";") + "m"
	}


	private fun getForegroundSet(color: Color?) = if (color == null) null else "38;" + getRgbColorSequence(color)
	private fun getBackgroundSet(color: Color?) = if (color == null) null else "48;" + getRgbColorSequence(color)

	private fun getForegroundReset(color: Color?) = if (color == null) null else "39"
	private fun getBackgroundReset(color: Color?) = if (color == null) null else "49"

	private fun getRgbColorSequence(color: Color) =
		"2;" + (color.red * 255).toInt() + ";" + (color.green * 255).toInt() + ";" + (color.blue * 255).toInt()

	private val csi = "\u001B["
}
