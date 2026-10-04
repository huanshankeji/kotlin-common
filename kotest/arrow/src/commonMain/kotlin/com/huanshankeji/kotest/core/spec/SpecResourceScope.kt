package com.huanshankeji.kotest.core.spec

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.resourceScope
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec

/**
 * Converts this [Spec] to an Arrow [ResourceScope] whose lifetime is the lifetime of this spec instance,
 * so that a spec can reuse the `suspend fun ResourceScope.xxx()` functions of an application.
 *
 * The resources installed into the returned scope, for example with [ResourceScope.install] in a `beforeSpec` callback or in a test,
 * and the finalizers registered with the non-suspending [ResourceScope.onRelease] are released
 * in an `afterSpec` callback registered by this function when all the tests of this spec instance have completed.
 * The release functions are run in the reverse order of the installation with [ExitCase.Completed].
 * All of them are run even if some of them throw, and the exceptions are rethrown together.
 * The callback runs before the `afterSpec` callbacks of the extensions registered after the first call of this function,
 * such as the ones installed with `install`, so the scope can be used to release resources that depend on them.
 *
 * This function is idempotent: it returns the same scope for the same spec instance.
 * Call it while the spec is initialized, for example in the spec's constructor lambda, and keep the returned scope,
 * because registering the underlying [AfterSpecListener] is not thread-safe.
 *
 * Like any Kotest `afterSpec` callback, the callback is skipped if a `beforeSpec` callback of the spec throws.
 * To release resources before the end of the spec, use Arrow's [resourceScope] in a test or a container instead.
 */
fun Spec.asResourceScope(): ResourceScope =
    extensions().filterIsInstance<SpecResourceScope>().singleOrNull() ?: extension(SpecResourceScope())

private class SpecResourceScope : ResourceScope, AfterSpecListener {
    private val finalizers = Atomic<List<suspend (ExitCase) -> Unit>>(emptyList())

    override fun onRelease(release: suspend (ExitCase) -> Unit) {
        finalizers.update { it + release }
    }

    override suspend fun afterSpec(spec: Spec) {
        val finalizers = finalizers.getAndSet(emptyList())
        // Arrow runs the finalizers in the reverse order of the `onRelease` calls and rethrows their exceptions together.
        resourceScope { finalizers.forEach { onRelease(it) } }
    }
}
