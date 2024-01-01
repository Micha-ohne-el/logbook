package moe.micha.logbook.outlets

import moe.micha.logbook.Color
import moe.micha.logbook.ColorInfo

open class AnsiConsoleOutlet : ConsoleOutlet() {
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
