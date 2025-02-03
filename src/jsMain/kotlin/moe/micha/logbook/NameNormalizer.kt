package moe.micha.logbook

import kotlin.reflect.KClass

actual val defaultNameNormalizer = object : NameNormalizer {
	override operator fun invoke(kClass: KClass<out Logbook>): String {
		for (name in listOfNotNull(kClass.simpleName)) {
			for (part in name.split(".").asReversed()) {
				val withoutSuffix = part.removeBadNames()

				if (withoutSuffix.isNotBlank()) return withoutSuffix.replaceFirstChar(Char::uppercase)
			}
		}

		return "<Unnamed Logger ${kClass.hashCode()}>"
	}
}
