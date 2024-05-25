package moe.micha.logbook

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import moe.micha.logbook.pretty.Chunk
import moe.micha.logbook.pretty.Color
import moe.micha.logbook.pretty.ColorInfo

class LogbookTests : DescribeSpec({
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

		it("is the class name") {
			class Test1 : Logbook()

			Test1().name shouldBe "Test1"
		}

		it("removes the suffix") {
			class TestLog : Logbook()
			class TestLogger : Logbook()
			class TestLogbook : Logbook()

			TestLog().name shouldBe "Test"
			TestLogger().name shouldBe "Test"
			TestLogbook().name shouldBe "Test"
		}

		it("uses package name if class name is a discouraged one") {
			// this will break if the package name changes. It's not ideal but I can't really think of a good way to avoid this.
			Log().name shouldBe "Micha"
		}
	}
})

// needs to be placed down here in order to have a qualifiedName.
class Log : Logbook()