package com.huanshankeji.kotlinx.serialization

import kotlinx.serialization.Serializable

@Serializable
class SerializableNothing private constructor()

typealias SerializableNoData = SerializableNothing?
