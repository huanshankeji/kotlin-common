package com.huanshankeji.os

fun getSystemOsName() =
    System.getProperty("os.name")

fun isOsLinux(systemOsName: String) =
    systemOsName == "Linux"

fun isCurrentOsLinux() =
    isOsLinux(getSystemOsName())

@Deprecated("Use `isOsLinux` instead.", ReplaceWith("isCurrentOsLinux()"))
fun isOSLinux(): Boolean =
    isCurrentOsLinux()

fun isOsMacos(systemOsName: String) =
    systemOsName == "Mac OS X"

fun isCurrentOsMacos() =
    isOsMacos(getSystemOsName())

fun isOsWindows(systemOsName: String) =
    systemOsName.startsWith("Windows")

fun isCurrentOsWindows() =
    isOsWindows(getSystemOsName())


enum class Os {
    Linux, Macos, Windows
}

fun getCurrentOS(): Os {
    val systemOsName = getSystemOsName()
    return when {
        isOsLinux(systemOsName) -> Os.Linux
        isOsMacos(systemOsName) -> Os.Macos
        isOsWindows(systemOsName) -> Os.Windows
        else -> throw IllegalArgumentException("unknown \"os.name\"")
    }
}
