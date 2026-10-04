package com.huanshankeji.kotest.core.spec

import arrow.fx.coroutines.ExitCase
import io.kotest.common.KotestInternal
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SpecResourceScopeTest {
    private suspend fun Spec.runAfterSpecListeners() =
        extensions().filterIsInstance<AfterSpecListener>().forEach { it.afterSpec(this) }

    @Test
    fun testAsResourceScopeIsIdempotent() {
        val spec = object : FunSpec() {}
        assertSame(spec.asResourceScope(), spec.asResourceScope())
        assertEquals(1, spec.extensions().size)
        assertTrue(object : FunSpec() {}.asResourceScope() !== spec.asResourceScope())
    }

    @Test
    fun testResourcesAreReleasedInReverseOrderOnlyAfterSpec() = runTest {
        val spec = object : FunSpec() {}
        val events = mutableListOf<String>()
        val resourceScope = spec.asResourceScope()

        assertEquals("a", resourceScope.install({ "a" }) { a, exitCase -> events += "release $a $exitCase" })
        resourceScope.onRelease { events += "release without a resource" }
        assertEquals("b", resourceScope.install({ "b" }) { b, exitCase -> events += "release $b $exitCase" })
        assertEquals(emptyList(), events)

        spec.runAfterSpecListeners()
        assertEquals(
            listOf(
                "release b ${ExitCase.Completed}",
                "release without a resource",
                "release a ${ExitCase.Completed}"
            ),
            events
        )

        // nothing is released twice
        spec.runAfterSpecListeners()
        assertEquals(3, events.size)
    }

    @Test
    fun testAllResourcesAreReleasedAndAllExceptionsAreRethrownWhenSomeReleasesFail() = runTest {
        val spec = object : FunSpec() {}
        val events = mutableListOf<String>()
        val resourceScope = spec.asResourceScope()
        resourceScope.onRelease { events += "first" }
        resourceScope.onRelease { throw IllegalStateException("second") }
        resourceScope.onRelease { throw IllegalArgumentException("third") }
        resourceScope.onRelease { events += "fourth" }

        val exception = assertFailsWith<IllegalArgumentException> { spec.runAfterSpecListeners() }
        assertEquals("third", exception.message)
        assertEquals(listOf("second"), exception.suppressedExceptions.map { it.message })
        assertEquals(listOf("fourth", "first"), events)
    }

    /** Resources installed lazily from a test (not from the spec's initialization) are released by a real Kotest engine run too. */
    @OptIn(KotestInternal::class)
    @Test
    fun testResourcesInstalledInTestsAreReleasedByTheKotestEngine() = runTest {
        EngineRunSpec.events.clear()
        val result = TestEngineLauncher()
            .withSpecRefs(SpecRef.Function({ EngineRunSpec() }, EngineRunSpec::class, "EngineRunSpec"))
            .execute()

        assertEquals(emptyList(), result.errors)
        assertEquals(
            listOf(
                "install vertx", "test 1", "install pool", "test 2",
                "release pool", "release vertx", "after spec listener"
            ),
            EngineRunSpec.events
        )
    }
}

private class EngineRunSpec : FunSpec() {
    companion object {
        val events = mutableListOf<String>()
    }

    init {
        val resourceScope = asResourceScope()
        // registered after the resource scope, so its `afterSpec` runs after the release of the resources
        extension(object : AfterSpecListener {
            override suspend fun afterSpec(spec: Spec) {
                events += "after spec listener"
            }
        })

        test("test 1") {
            resourceScope.install({ events += "install vertx" }) { _, _ -> events += "release vertx" }
            events += "test 1"
        }
        test("test 2") {
            resourceScope.install({ events += "install pool" }) { _, _ -> events += "release pool" }
            events += "test 2"
        }
    }
}
