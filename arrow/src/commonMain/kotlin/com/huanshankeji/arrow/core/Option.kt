package com.huanshankeji.arrow.core

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

fun Boolean.toOptionUnit(): Option<Unit> =
    if (this) Some(Unit) else None
