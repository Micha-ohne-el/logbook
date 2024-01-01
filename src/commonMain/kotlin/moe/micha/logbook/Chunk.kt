package moe.micha.logbook

data class Chunk(
	val text: String,
	val colorInfo: ColorInfo? = null,
) {
	constructor(text: String, color: Color) : this(text, ColorInfo(foreground = color))
}
