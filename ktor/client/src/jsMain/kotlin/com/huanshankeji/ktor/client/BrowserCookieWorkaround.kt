package com.huanshankeji.ktor.client

// see: https://youtrack.jetbrains.com/issue/KTOR-539#focus=Comments-27-4683851.0-0
@Deprecated(
    "KTOR-539 is fixed in Ktor 3.2.0. " +
        "Use `HttpClient(Js) { engine { configureRequest { credentials = \"include\" } } }`.",
)
fun addCredentialsIncludeToFetch() =
    js(
        """
var originalFetch = window.fetch;
window.fetch = function (resource, init) {
    return originalFetch(resource, Object.assign({ credentials: 'include' }, init || {}));
};
"""
    )

/* buggy Kotlin/JS implementation
fun addCredentialsIncludeToFetch() {
    val originalFetch = window.asDynamic().fetch
    window.asDynamic().fetch = { resource: dynamic, init: RequestInit? ->
        val newInit =
            if (init === null) json("credentials" to "include").asDynamic()
            else init.apply { asDynamic().credentials = "include" }
        // Will this work?: init.credentials = "include" as RequestCredentials
        originalFetch(resource, newInit)
    }
}
*/
