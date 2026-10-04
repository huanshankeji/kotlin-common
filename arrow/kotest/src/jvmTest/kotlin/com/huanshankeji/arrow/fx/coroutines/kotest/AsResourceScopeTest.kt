package com.huanshankeji.arrow.fx.coroutines.kotest

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

private object SpecLevelRelease {
    val events = mutableListOf<String>()
}

class SpecAsResourceScopeTest : FunSpec({
    val scope = asResourceScope()
    lateinit var acquired: String
    beforeSpec {
        acquired = scope.install({ "spec-resource" }) { _, exit ->
            SpecLevelRelease.events += "released:${exit::class.simpleName}"
        }
    }
    test("the spec ResourceScope resource is available") {
        acquired shouldBe "spec-resource"
        SpecLevelRelease.events.shouldBeEmpty()
    }
    afterSpec {
        SpecLevelRelease.events.shouldBeEmpty()
    }
    afterProject {
        SpecLevelRelease.events shouldBe listOf("released:Completed")
    }
})

class TestScopeAsResourceScopeTest : FunSpec({
    testCaseOrder = TestCaseOrder.Sequential

    val released = mutableListOf<String>()

    context("container") {
        val scope = asResourceScope()
        val outer = scope.install({ "outer" }) { value, _ -> released += value }
        val inner = scope.install({ "inner" }) { value, _ -> released += value }
        test("resources are available") {
            outer shouldBe "outer"
            inner shouldBe "inner"
            released.shouldBeEmpty()
        }
        context("nested") {
            test("resources stay installed for nested tests") {
                outer shouldBe "outer"
                released.shouldBeEmpty()
            }
        }
    }

    test("resources are released in reverse installation order after the container") {
        released shouldBe listOf("inner", "outer")
    }
})
