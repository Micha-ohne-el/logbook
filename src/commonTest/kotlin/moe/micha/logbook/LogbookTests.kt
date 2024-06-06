package moe.micha.logbook

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import moe.micha.logbook.pretty.Chunk
import moe.micha.logbook.pretty.Color
import moe.micha.logbook.pretty.ColorInfo

class LogbookTests : DescribeSpec({
	describe("constructor") {
		it("can be called with 0 arguments") {
			object : Logbook() {}
		}

		it("can be passed a name normalizer") {
			val normalizer = NameNormalizer { "test" }
			object : Logbook(normalizer) {}
		}
	}

	context("WithDefaults") {
		describe("constructor") {
			it("can be called with 0 arguments") {
				object : Logbook.WithDefaults() {}
			}

			it("can be passed a name normalizer") {
				val normalizer = NameNormalizer { "test" }
				object : Logbook.WithDefaults(normalizer) {}
			}
		}
	}

	describe(Logbook::toChunk) {
		it("returns a chunk with the text and colorInfo") {
			val logbook = object : TestLogbook() {}

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
			val logbook = object : TestLogbook() {}

			logbook.formatter shouldBe null

			logbook.formatWith { listOf() }

			logbook.formatter shouldNotBe null
		}
	}

	describe(Logbook::minimumLevel) {
		it("defaults to the first log level") {
			val logbook1 = object : TestLogbook() {}

			logbook1.minimumLevel shouldBe null

			val logbook2 = object : TestLogbook() {
				val testLevel by level("testLevel")
			}

			logbook2.minimumLevel!!.name shouldBe "testLevel"

			val logbook3 = object : TestLogbook() {
				val levelX by level("x")
				val levelY by level("y")
				val levelA by level("a")
			}

			logbook3.minimumLevel!!.name shouldBe "x"
		}

		it("enables it and all levels above") {
			val logbook = object : TestLogbook() {
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
			val logbook = object : TestLogbook() {
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

	describe(Logbook::name) {
		it("can be set") {
			class TestLogger : Logbook() {
				override val name = "testing 123"
			}

			TestLogger().name shouldBe "testing 123"
		}

		it("uses NameNormalizer to infer name") {
			val nameNormalizer = NameNormalizer { "success" }

			val testLog = object : Logbook(nameNormalizer) {}

			testLog.name shouldBe "success"
		}
	}
})
