package moe.micha.logbook.pretty

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.shouldBe
import moe.micha.logbook.describe

class ColorTests : DescribeSpec({
	describe(::Color) {
		it("throws when an argument is below 0.0") {
			shouldThrow<IllegalArgumentException> { Color(-1.0, 0.0, 0.0) }
			shouldThrow<IllegalArgumentException> { Color(0.0, -1.0, 0.0) }
			shouldThrow<IllegalArgumentException> { Color(0.0, 0.0, -1.0) }
		}

		it("throws when an argument is above 1.0") {
			shouldThrow<IllegalArgumentException> { Color(1.1, 0.0, 0.0) }
			shouldThrow<IllegalArgumentException> { Color(0.0, 1.1, 0.0) }
			shouldThrow<IllegalArgumentException> { Color(0.0, 0.0, 1.1) }
		}

		it("throws when an argument is non-finite") {
			shouldThrow<IllegalArgumentException> { Color(Double.POSITIVE_INFINITY, 0.0, 0.0) }
			shouldThrow<IllegalArgumentException> { Color(0.0, Double.NEGATIVE_INFINITY, 0.0) }
			shouldThrow<IllegalArgumentException> { Color(0.0, 0.0, Double.NaN) }
		}

		it("retains values passed in") {
			Color(0.1, 0.2, 0.3).apply {
				red shouldBe 0.1
				green shouldBe 0.2
				blue shouldBe 0.3
			}
		}
	}

	describe(Color::hue) {
		it("is 0.0 for any kind of red") {
			Color(1.0, 0.0, 0.0).hue shouldBe 0.0
			Color(0.5, 0.0, 0.0).hue shouldBe 0.0
			Color(0.1, 0.0, 0.0).hue shouldBe 0.0
		}

		it("is 0.5 for any kind of cyan") {
			Color(0.0, 1.0, 1.0).hue shouldBe 0.5
			Color(0.0, 0.5, 0.5).hue shouldBe 0.5
			Color(0.0, 0.1, 0.1).hue shouldBe 0.5
		}

		it("is NaN for any kind of gray") {
			Color(0.0, 0.0, 0.0).hue.shouldBeNaN()
			Color(0.5, 0.5, 0.5).hue.shouldBeNaN()
			Color(1.0, 1.0, 1.0).hue.shouldBeNaN()
		}
	}

	describe(Color::saturation) {
		it("is 1.0 for primary colors") {
			Color(1.0, 0.0, 0.0).saturation shouldBe 1.0
			Color(0.0, 1.0, 0.0).saturation shouldBe 1.0
			Color(0.0, 0.0, 1.0).saturation shouldBe 1.0
		}

		it("is 0.0 for any kind of gray") {
			Color(0.1, 0.1, 0.1).saturation shouldBe 0.0
			Color(0.5, 0.5, 0.5).saturation shouldBe 0.0
			Color(0.9, 0.9, 0.9).saturation shouldBe 0.0
		}

		it("is NaN for white and black") {
			Color(1.0, 1.0, 1.0).saturation.shouldBeNaN()
			Color(0.0, 0.0, 0.0).saturation.shouldBeNaN()
		}
	}

	describe(Color::luminance) {
		it("is 0.5 for primary colors") {
			Color(1.0, 0.0, 0.0).luminance shouldBe 0.5
			Color(0.0, 1.0, 0.0).luminance shouldBe 0.5
			Color(0.0, 0.0, 1.0).luminance shouldBe 0.5
		}

		it("is correct for any kind of gray") {
			Color(0.1, 0.1, 0.1).luminance shouldBe 0.1
			Color(0.5, 0.5, 0.5).luminance shouldBe 0.5
			Color(0.9, 0.9, 0.9).luminance shouldBe 0.9
		}
	}

	describe(Color.Companion::class) {
		describe(Color.Companion::fromHsl) {
			it("can make primary colors") {
				Color.fromHsl(0.0 / 6.0, 1.0, 0.5) shouldBe Color(1.0, 0.0, 0.0)
				Color.fromHsl(1.0 / 6.0, 1.0, 0.5) shouldBe Color(1.0, 1.0, 0.0)
				Color.fromHsl(2.0 / 6.0, 1.0, 0.5) shouldBe Color(0.0, 1.0, 0.0)
				Color.fromHsl(3.0 / 6.0, 1.0, 0.5) shouldBe Color(0.0, 1.0, 1.0)
				Color.fromHsl(4.0 / 6.0, 1.0, 0.5) shouldBe Color(0.0, 0.0, 1.0)
				Color.fromHsl(5.0 / 6.0, 1.0, 0.5) shouldBe Color(1.0, 0.0, 1.0)
			}

			it("wraps the hue value") {
				val base = Color.fromHsl(0.0, 1.0, 0.5)
				Color.fromHsl(1.0, 1.0, 0.5) shouldBe base
				Color.fromHsl(2.0, 1.0, 0.5) shouldBe base
				Color.fromHsl(100.0, 1.0, 0.5) shouldBe base
				Color.fromHsl(-123.0, 1.0, 0.5) shouldBe base
			}

			it("clamps the saturation value") {
				val baseMax = Color.fromHsl(0.0, 1.0, 0.5)
				val baseMin = Color.fromHsl(0.0, 0.0, 0.5)
				Color.fromHsl(0.0, 1.0, 0.5) shouldBe baseMax
				Color.fromHsl(0.0, 2.0, 0.5) shouldBe baseMax
				Color.fromHsl(0.0, 100.0, 0.5) shouldBe baseMax
				Color.fromHsl(0.0, -1.0, 0.5) shouldBe baseMin
				Color.fromHsl(0.0, -2.0, 0.5) shouldBe baseMin
				Color.fromHsl(0.0, -100.0, 0.5) shouldBe baseMin
			}

			it("clamps the luminance value") {
				val baseMax = Color.fromHsl(0.0, 1.0, 1.0)
				val baseMin = Color.fromHsl(0.0, 1.0, 0.0)
				Color.fromHsl(0.0, 1.0, 1.0) shouldBe baseMax
				Color.fromHsl(0.0, 1.0, 2.0) shouldBe baseMax
				Color.fromHsl(0.0, 1.0, 100.0) shouldBe baseMax
				Color.fromHsl(0.0, 1.0, -1.0) shouldBe baseMin
				Color.fromHsl(0.0, 1.0, -2.0) shouldBe baseMin
				Color.fromHsl(0.0, 1.0, -100.0) shouldBe baseMin
			}
		}

		describe(Color.Companion::fromRgb) {
			it("can make colors") {
				Color.fromRgb(0x000000) shouldBe Color(0.0, 0.0, 0.0)
				Color.fromRgb(0xFFFFFF) shouldBe Color(1.0, 1.0, 1.0)
				Color.fromRgb(0x336699) shouldBe Color(0.2, 0.4, 0.6)
			}

			it("throws when value is outside valid range") {
				shouldThrow<IllegalArgumentException> { Color.fromRgb(0x1000000) }
				shouldThrow<IllegalArgumentException> { Color.fromRgb(-0x1) }
			}
		}
	}
})
