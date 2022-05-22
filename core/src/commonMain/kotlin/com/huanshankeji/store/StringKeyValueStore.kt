package com.huanshankeji.store

interface StringKeyValueStore {
    operator fun set(key: String, value: String)
    fun getOrNull(key: String): String?
    fun exists(key: String): Boolean
    fun getNonNull(key: String): String
    fun remove(key: String)
}