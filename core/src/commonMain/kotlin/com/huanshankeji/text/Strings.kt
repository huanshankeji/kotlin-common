package com.huanshankeji.text

// This function is temporary until internalization is done.
fun String.capitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
