package com.huanshankeji.security

import java.security.MessageDigest

fun sha256(): MessageDigest =
    MessageDigest.getInstance("SHA-256")

fun sha256Hash(byteArray: ByteArray): ByteArray =
    sha256().digest(byteArray)
