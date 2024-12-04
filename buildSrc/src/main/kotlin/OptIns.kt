val optIns = listOf("com.huanshankeji.InternalApi", "com.huanshankeji.ExperimentalApi")

inline fun forEachOptIn(action: (String) -> Unit) =
    optIns.forEach(action)
