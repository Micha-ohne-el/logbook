import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import moe.micha.logbook.Logbook
import moe.micha.logbook.pretty.Chunk
import moe.micha.logbook.pretty.Color
import moe.micha.logbook.pretty.ColorInfo

class LogbookTests : DescribeSpec({
	describe(Logbook::toChunk) {
		it("returns a chunk with the text and colorInfo") {
			val logbook = Logbook("test")

			logbook.toChunk() shouldBe Chunk("test", null)

			logbook.colorInfo = ColorInfo()

			logbook.toChunk() shouldBe Chunk("test", ColorInfo())

			logbook.colorInfo = ColorInfo(foreground = Color.pureGreen, background = Color.pureMagenta)

			logbook.toChunk() shouldBe Chunk(
				"test",
				ColorInfo(foreground = Color.pureGreen, background = Color.pureMagenta),
			)
		}
	}

	describe(Logbook::formatWith) {
		it("sets the formatter") {
			val logbook = Logbook("test")

			logbook.formatter shouldBe null

			logbook.formatWith { listOf() }

			logbook.formatter shouldNotBe null
		}
	}

	describe(Logbook::minimumLevel) {
		it("defaults to the first log level") {
			var logbook = Logbook("no log levels")

			logbook.minimumLevel shouldBe null

			logbook = object : Logbook("one log level") {
				val testLevel by level("testLevel")
			}

			logbook.minimumLevel!!.name shouldBe "testLevel"

			logbook = object : Logbook("several log levels") {
				val levelX by level("x")
				val levelY by level("y")
				val levelA by level("a")
			}

			logbook.minimumLevel!!.name shouldBe "x"
		}

		it("enables it and all levels above") {
			val logbook = object : Logbook("test") {
				val level0 by level("0") { isEnabled = false }
				val level1 by level("1") { isEnabled = false }
				val level2 by level("2") { isEnabled = false }
			}

			logbook.level0.isEnabled shouldBe false
			logbook.level1.isEnabled shouldBe false
			logbook.level2.isEnabled shouldBe false

			logbook.minimumLevel = logbook.level1

			logbook.level0.isEnabled shouldBe false
			logbook.level1.isEnabled shouldBe true
			logbook.level2.isEnabled shouldBe true
		}

		it("disables all levels below") {
			val logbook = object : Logbook("test") {
				val level0 by level("0")
				val level1 by level("1")
				val level2 by level("2")
			}

			logbook.level0.isEnabled shouldBe true
			logbook.level1.isEnabled shouldBe true
			logbook.level2.isEnabled shouldBe true

			logbook.minimumLevel = logbook.level2

			logbook.level0.isEnabled shouldBe false
			logbook.level1.isEnabled shouldBe false
			logbook.level2.isEnabled shouldBe true
		}
	}
})
