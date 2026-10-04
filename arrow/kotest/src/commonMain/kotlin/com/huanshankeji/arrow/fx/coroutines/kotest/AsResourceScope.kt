package com.huanshankeji.arrow.fx.coroutines.kotest

import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.allocate
import arrow.fx.coroutines.resource
import com.huanshankeji.ExperimentalApi
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.test.TestResult
import kotlinx.coroutines.DelicateCoroutinesApi

/**
 * Converts this [Spec] to an Arrow [ResourceScope] whose lifetime is the spec instance.
 *
 * Call this from the spec body, which is not suspending. Then [ResourceScope.install] resources from
 * `beforeSpec`, tests, or other suspending callbacks. Installed resources are released after the spec
 * finishes (including `afterSpec`), in reverse acquisition order, including when the spec fails.
 *
 * For resources that should live only as long as a `context` or test, use [TestScope.asResourceScope] instead.
 */
@ExperimentalApi
@OptIn(DelicateCoroutinesApi::class)
fun Spec.asResourceScope(): ResourceScope =
    DeferredResourceScope().also { deferred ->
        extension(object : SpecExtension {
            override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
                val (scope, cancelAll) = resource { this }.allocate()
                deferred.delegate = scope
                var exitCase: ExitCase = ExitCase.Completed
                try {
                    execute(spec)
                } catch (e: Throwable) {
                    exitCase = ExitCase.ExitCase(e)
                    throw e
                } finally {
                    cancelAll(exitCase)
                }
            }
        })
    }

/**
 * Converts this Kotest [TestScope] (a `context` / container or a test) to an Arrow [ResourceScope]
 * whose lifetime is this scope.
 *
 * Call this before registering nested tests. Installed resources stay available to nested tests and
 * are released after this scope finishes, in reverse acquisition order.
 *
 * For resources that only live inside a single leaf test, prefer Arrow's `resourceScope { }` block.
 */
@ExperimentalApi
@OptIn(DelicateCoroutinesApi::class)
suspend fun TestScope.asResourceScope(): ResourceScope {
    val (scope, cancelAll) = resource { this }.allocate()
    val thisDescriptor = testCase.descriptor
    testCase.spec.extension(object : AfterTestListener {
        override suspend fun afterAny(testCase: TestCase, result: TestResult) {
            if (testCase.descriptor == thisDescriptor)
                cancelAll(result.toExitCase())
        }
    })
    return scope
}

private fun TestResult.toExitCase(): ExitCase =
    errorOrNull?.let { ExitCase.ExitCase(it) } ?: ExitCase.Completed

private class DeferredResourceScope : ResourceScope {
    var delegate: ResourceScope? = null

    private fun bound(): ResourceScope =
        delegate
            ?: error(
                "This ResourceScope is not active yet. Call asResourceScope() on the Spec and install resources from beforeSpec, a test, or another suspending callback."
            )

    override fun onRelease(release: suspend (ExitCase) -> Unit) {
        bound().onRelease(release)
    }
}
