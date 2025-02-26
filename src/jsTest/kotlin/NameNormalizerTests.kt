import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass
import moe.micha.logbook.Logbook
import moe.micha.logbook.NameNormalizer
import moe.micha.logbook.defaultNameNormalizer
import moe.micha.logbook.describe

class NameNormalizerTests : DescribeSpec({
	describe(NameNormalizer::invoke) {
		it("returns null if simpleName is null") {
			val kClass = Logbook::class.mock(simpleName = null)

			defaultNameNormalizer(kClass) shouldBe null
		}

		it("returns simpleName") {
			val kClass = Logbook::class.mock(simpleName = "Test")

			defaultNameNormalizer(kClass) shouldBe "Test"
		}

		it("strips suffixes off simpleName") {
			val kClass1 = Logbook::class.mock(simpleName = "TestLog")
			val kClass2 = Logbook::class.mock(simpleName = "TestLogger")
			val kClass3 = Logbook::class.mock(simpleName = "TestLogbook")

			defaultNameNormalizer(kClass1) shouldBe "Test"
			defaultNameNormalizer(kClass2) shouldBe "Test"
			defaultNameNormalizer(kClass3) shouldBe "Test"
		}

		it("converts simpleName to title case") {
			val kClass = Logbook::class.mock(simpleName = "test")

			defaultNameNormalizer(kClass) shouldBe "Test"
		}

		it("returns special name if simpleName is null") {
			val kClass = Logbook::class.mock(simpleName = null, hashCode = 12345)

			defaultNameNormalizer(kClass) shouldBe "<Unnamed Logger 12345>"
		}

		it("returns special name if simpleName is a bad name") {
			val kClass1 = Logbook::class.mock(simpleName = "Log", hashCode = 1)
			val kClass2 = Logbook::class.mock(simpleName = "Logger", hashCode = 2)
			val kClass3 = Logbook::class.mock(simpleName = "Logbook", hashCode = 3)
			val kClass4 = Logbook::class.mock(simpleName = "Companion", hashCode = 4)

			defaultNameNormalizer(kClass1) shouldBe "<Unnamed Logger 1>"
			defaultNameNormalizer(kClass2) shouldBe "<Unnamed Logger 2>"
			defaultNameNormalizer(kClass3) shouldBe "<Unnamed Logger 3>"
			defaultNameNormalizer(kClass4) shouldBe "<Unnamed Logger 4>"
		}
	}
})

private fun <T : Any> KClass<T>.mock(simpleName: String?, hashCode: Int? = null) = object : KClass<T> by this {
	override val simpleName = simpleName

	override fun hashCode() = hashCode ?: super.hashCode()
}
