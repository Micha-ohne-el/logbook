package moe.micha.logbook

import kotlin.reflect.KClass

fun interface NameNormalizer {
	operator fun invoke(kClass: KClass<out Logbook>): String

	fun String.removeLogSuffix(): String =
		if (endsWith("log", ignoreCase = true)) {
			substring(0, length - 3).removeLogSuffix()
		} else if (endsWith("logger", ignoreCase = true)) {
			substring(0, length - 6).removeLogSuffix()
		} else if (endsWith("logbook", ignoreCase = true)) {
			substring(0, length - 7).removeLogSuffix()
		} else {
			this
		}
}

expect val defaultNameNormalizer: NameNormalizer
