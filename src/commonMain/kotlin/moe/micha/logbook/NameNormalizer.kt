package moe.micha.logbook

import kotlin.reflect.KClass

fun interface NameNormalizer {
	operator fun invoke(kClass: KClass<out Logbook>): String

	fun String.removeBadNames(): String =
		if (endsWith("log", ignoreCase = true)) {
			substring(0, length - 3).removeBadNames()
		} else if (endsWith("logger", ignoreCase = true)) {
			substring(0, length - 6).removeBadNames()
		} else if (endsWith("logbook", ignoreCase = true)) {
			substring(0, length - 7).removeBadNames()
		} else if (equals("Companion", ignoreCase = false)) {
			substring(0, length - 9)
		} else {
			this
		}
}

expect val defaultNameNormalizer: NameNormalizer
