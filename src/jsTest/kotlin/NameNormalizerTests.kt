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

		it("returns simpleName name") {
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
	}
})

private fun <T : Any> KClass<T>.mock(simpleName: String?) = object : KClass<T> by this {
	override val simpleName = simpleName
}
