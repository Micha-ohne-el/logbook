import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import moe.micha.logbook.LogEntry
import moe.micha.logbook.LogLevel
import moe.micha.logbook.LogOutlet
import moe.micha.logbook.Logbook
import moe.micha.logbook.pretty.Chunk

class FormattingTests : DescribeSpec({
	lateinit var outlet: TestOutlet

	beforeEach {
		outlet = TestOutlet()
	}

	describe("Logbook.${Logbook::formatter.name}") {
		it("formats the log entry") {
			val logbook = object : TestLogbook() {
				override var formatter: ((LogEntry) -> Iterable<Chunk>)? = { entry ->
					listOf(Chunk("TEST: "), Chunk(entry.data.toString()))
				}

				val level by level("level", outlet)
			}

			logbook.level("test")

			outlet.logs shouldBe listOf(listOf(Chunk("TEST: "), Chunk("test")))
		}
	}

	describe("LogLevel.${LogLevel::formatter.name}") {
		it("formats the log entry") {
			val logbook = object : TestLogbook() {
				val level by level("level", outlet) {
					formatWith { entry ->
						listOf(Chunk("TEST2: "), Chunk(entry.data.toString()))
					}
				}
			}

			logbook.level("test2")

			outlet.logs shouldBe listOf(listOf(Chunk("TEST2: "), Chunk("test2")))
		}
	}

	describe("LogOutlet.${LogOutlet::formatter.name}") {
		@Suppress("NAME_SHADOWING")
		val outlet = TestOutlet().apply {
			formatWith { entry ->
				listOf(Chunk("TEST3: "), Chunk(entry.data.toString()))
			}
		}

		it("formats the log entry") {
			val logbook = object : TestLogbook() {
				val level by level("level", outlet)
			}

			logbook.level("test3")

			outlet.logs shouldBe listOf(listOf(Chunk("TEST3: "), Chunk("test3")))
		}
	}

	it("prioritizes LogOutlet, then LogLevel, then Logbook") {
		outlet.formatWith { entry ->
			listOf(Chunk("Outlet: "), Chunk(entry.data.toString()))
		}

		val logbook = object : TestLogbook() {
			init {
				formatWith { entry ->
					listOf(Chunk("Logbook: "), Chunk(entry.data.toString()))
				}
			}

			val level by level("level", outlet) {
				formatWith { entry ->
					listOf(Chunk("Level: "), Chunk(entry.data.toString()))
				}
			}
		}

		logbook.level("test")
		outlet.logs.last() shouldBe listOf(Chunk("Outlet: "), Chunk("test"))

		outlet.formatter = null

		logbook.level("test")
		outlet.logs.last() shouldBe listOf(Chunk("Level: "), Chunk("test"))

		logbook.level.formatter = null

		logbook.level("test")
		outlet.logs.last() shouldBe listOf(Chunk("Logbook: "), Chunk("test"))
	}
})
