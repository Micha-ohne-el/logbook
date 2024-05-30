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
	}
})

private fun <T : Any> KClass<T>.mock(simpleName: String?) = object : KClass<T> by this {
	override val simpleName = simpleName
}
