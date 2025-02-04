package moe.micha.logbook.pretty

data class ColorInfo(
	val foreground: Color? = null,
	val background: Color? = null,
) {
	constructor(
		foreground: Int,
		background: Int? = null,
	) : this(Color.fromRgb(foreground), background?.let(Color::fromRgb))
}
