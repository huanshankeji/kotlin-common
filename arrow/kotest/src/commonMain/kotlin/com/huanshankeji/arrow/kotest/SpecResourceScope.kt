package com.huanshankeji.arrow.kotest

import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.allocate
import arrow.fx.coroutines.resource
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.concurrent.Volatile

/**
 * Converts this Kotest [Spec] instance into an Arrow [ResourceScope].
 *
 * Finalizers registered with [ResourceScope.onRelease], and resources installed with [ResourceScope.install],
 * run after this spec instance finishes. That includes a failing `beforeSpec`, which skips `afterSpec`.
 * [ResourceScope.install] is suspending; from a spec constructor, register finalizers with [ResourceScope.onRelease].
 *
 * The [ExitCase] is [ExitCase.Completed] when the spec interceptor returns normally. Kotest records test
 * failures as test results rather than throwing out of the interceptor, so those still release as
 * [ExitCase.Completed]. Cancellation or an exception that escapes the interceptor releases as
 * [ExitCase.Cancelled] or [ExitCase.Failure].
 */
@OptIn(DelicateCoroutinesApi::class)
fun Spec.toResourceScope(): ResourceScope =
    SpecResourceScope().also { scope ->
        extension(object : SpecExtension {
            override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
                val (delegate, release) = resource { this }.allocate()
                scope.attach(delegate)
                var exitCase: ExitCase = ExitCase.Completed
                try {
                    execute(spec)
                } catch (thrown: Throwable) {
                    exitCase = ExitCase.ExitCase(thrown)
                    throw thrown
                } finally {
                    release(exitCase)
                }
            }
        })
    }

private class SpecResourceScope : ResourceScope {
    @Volatile
    private var delegate: ResourceScope? = null
    private val earlyFinalizers = mutableListOf<suspend (ExitCase) -> Unit>()

    override fun onRelease(release: suspend (ExitCase) -> Unit) {
        val target = delegate
        if (target != null) target.onRelease(release) else earlyFinalizers.add(release)
    }

    fun attach(delegate: ResourceScope) {
        this.delegate = delegate
        earlyFinalizers.forEach { delegate.onRelease(it) }
        earlyFinalizers.clear()
    }
}
