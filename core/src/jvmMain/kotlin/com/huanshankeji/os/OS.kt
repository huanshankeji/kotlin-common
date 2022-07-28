package com.huanshankeji.os

fun isOSLinux(): Boolean =
    System.getProperty("os.name") == "Linux"
