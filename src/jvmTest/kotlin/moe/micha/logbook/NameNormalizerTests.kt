package moe.micha.logbook

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class NameNormalizerTests : DescribeSpec({
	describe(NameNormalizer::invoke) {
		it("returns simpleName name") {
			val kClass = Logbook::class.mock(simpleName = "Test", qualifiedName = null)

			defaultNameNormalizer(kClass) shouldBe "Test"
		}

		it("strips suffixes off simpleName") {
			val kClass1 = Logbook::class.mock(simpleName = "TestLog", qualifiedName = null)
			val kClass2 = Logbook::class.mock(simpleName = "TestLogger", qualifiedName = null)
			val kClass3 = Logbook::class.mock(simpleName = "TestLogbook", qualifiedName = null)

			defaultNameNormalizer(kClass1) shouldBe "Test"
			defaultNameNormalizer(kClass2) shouldBe "Test"
			defaultNameNormalizer(kClass3) shouldBe "Test"
		}

		it("converts simpleName to title case") {
			val kClass = Logbook::class.mock(simpleName = "test", qualifiedName = null)

			defaultNameNormalizer(kClass) shouldBe "Test"
		}

		it("uses qualifiedName if simpleName is forbidden") {
			val kClass1 = Logbook::class.mock(simpleName = "Log", qualifiedName = "a")
			val kClass2 = Logbook::class.mock(simpleName = "Logger", qualifiedName = "a")
			val kClass3 = Logbook::class.mock(simpleName = "Logbook", qualifiedName = "a")

			defaultNameNormalizer(kClass1) shouldBe "A"
			defaultNameNormalizer(kClass2) shouldBe "A"
			defaultNameNormalizer(kClass3) shouldBe "A"
		}

		it("splits qualifiedName") {
			val kClass = Logbook::class.mock(simpleName = "Log", qualifiedName = "a.b")

			defaultNameNormalizer(kClass) shouldBe "B"
		}

		it("strips suffixes off qualifiedName segments") {
			val kClass = Logbook::class.mock(simpleName = "Log", qualifiedName = "abc.TestLog")

			defaultNameNormalizer(kClass) shouldBe "Test"
		}

		it("skips forbidden qualifiedName segments") {
			val kClass = Logbook::class.mock(simpleName = "Log", qualifiedName = "a.b.c.log.LOGGER.LogBook.Log")

			defaultNameNormalizer(kClass) shouldBe "C"
		}

		it("returns special name if both simpleName and qualifiedName are null") {
			val kClass = Logbook::class.mock(simpleName = null, qualifiedName = null, hashCode = 12345)

			defaultNameNormalizer(kClass) shouldBe "<Unnamed Logger 12345>"
		}
	}
})

private fun <T : Any> KClass<T>.mock(simpleName: String?, qualifiedName: String?, hashCode: Int? = null) =
	object : KClass<T> by this {
		override val simpleName = simpleName
		override val qualifiedName = qualifiedName

		override fun hashCode() = hashCode ?: super.hashCode()
	}
