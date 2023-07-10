package com.huanshankeji.exposed

import org.jetbrains.exposed.sql.stringLiteral

// see: https://stackoverflow.com/questions/68539620/aliasing-count-for-several-columns-in-a-group-by-query-in-exposed
val asterisk = stringLiteral("*")
