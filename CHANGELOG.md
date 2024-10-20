# Change log

## v0.5.1 / 2024-10-19

* enable the iOS targets for the "coroutines" module, which was disabled due to a compiler bug

## v0.5.0 / 2024-10-19

* support the iOS and Wasm JS targets
* add the enum operator functions `+`, `-`, `++`, `--` since `enumEntries` got supported in Kotlin 2.0
* add `MutableMapStringKeyValueStore`
* add more functions for checking OSs in `com.huanshankeji.os`
* add some `isSorted...` extension functions that the standard library lacks
