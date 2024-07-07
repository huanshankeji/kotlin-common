package com.huanshankeji.store

class MutableMapStringKeyValueStore(val mutableMap: MutableMap<String, String> = mutableMapOf()) : StringKeyValueStore {
    override fun exists(key: String): Boolean =
        mutableMap.contains(key) // `containsKey` also works

    override fun getNonNull(key: String): String =
        mutableMap.getValue(key)

    override fun getOrNull(key: String): String? =
        mutableMap[key]

    override fun remove(key: String) {
        mutableMap.remove(key)
    }

    override fun set(key: String, value: String) {
        mutableMap[key] = value
    }
}