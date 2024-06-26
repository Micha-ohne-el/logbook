package moe.micha.logbook

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class LogLevelTests : DescribeSpec({
	val outlet = TestOutlet()

	val logbook = object : TestLogbook() {
		val debug by level("debug", outlet)
		val info by level("info", outlet)
	}

	beforeEach {
		outlet.logs.clear()
	}

	describe(LogLevel::formatWith) {
		it("sets formatter") {
			val logLevel = LogLevel(logbook, "test")

			logLevel.formatter shouldBe null

			logLevel.formatWith { listOf() }

			logLevel.formatter shouldNotBe null
		}
	}

	describe(LogLevel::invoke) {
		it("calls send on outlets") {
			logbook.formatWith {
				listOf()
			}

			logbook.debug("")

			outlet.logs.first() shouldBe listOf()
		}

		it("calls send on logbook outlets") {
			val logbookOutlet = TestOutlet()
			logbook.outlets += logbookOutlet
			logbook.formatWith {
				listOf()
			}

			logbook.debug("")

			outlet.logs.size shouldBe 1
			logbookOutlet.logs.size shouldBe 1
		}
	}
})
