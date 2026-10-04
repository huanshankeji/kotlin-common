package com.huanshankeji.arrow.kotest

import arrow.fx.coroutines.ExitCase
import io.kotest.common.KotestInternal
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SpecResourceScopeTest {
    @Test
    fun releasesConstructorAndInstalledFinalizersInLifoOrder() {
        events.clear()
        launch(OpenDuringTestSpec::class) { OpenDuringTestSpec() }
        assertEquals(listOf("acquired", "released", "constructor-2", "constructor-1"), events.toList())
    }

    @Test
    fun releasesWhenBeforeSpecFails() {
        events.clear()
        launch(BeforeSpecFailsSpec::class) { BeforeSpecFailsSpec() }
        assertEquals(listOf("released"), events.toList())
    }

    @Test
    fun releasesWhenATestFails() {
        events.clear()
        launch(TestFailsSpec::class) { TestFailsSpec() }
        assertEquals(listOf("released"), events.toList())
    }
}

private val events = mutableListOf<String>()

private class OpenDuringTestSpec : FunSpec({
    isolationMode = IsolationMode.SingleInstance
    val resources = toResourceScope()
    resources.onRelease { events += "constructor-1" }
    resources.onRelease { events += "constructor-2" }
    beforeSpec {
        resources.install({
            events += "acquired"
            Unit
        }) { _, exitCase ->
            assertIs<ExitCase.Completed>(exitCase)
            events += "released"
        }
    }
    test("resource stays open during the test") {
        assertEquals(listOf("acquired"), events.toList())
    }
})

private class BeforeSpecFailsSpec : FunSpec({
    isolationMode = IsolationMode.SingleInstance
    val resources = toResourceScope()
    resources.onRelease { events += "released" }
    beforeSpec { error("beforeSpec failed") }
    test("does not run") { error("test ran") }
})

private class TestFailsSpec : FunSpec({
    isolationMode = IsolationMode.SingleInstance
    val resources = toResourceScope()
    resources.onRelease { events += "released" }
    test("fails") { error("test failed") }
})

@OptIn(KotestInternal::class)
private fun launch(kclass: KClass<out Spec>, spec: () -> Spec) {
    val result = runBlocking {
        TestEngineLauncher()
            .withSpecRefs(SpecRef.Function(spec, kclass))
            .execute()
    }
    assertEquals(emptyList(), result.errors, result.errors.toString())
}
