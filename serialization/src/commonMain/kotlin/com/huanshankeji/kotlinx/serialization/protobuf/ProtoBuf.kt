package com.huanshankeji.kotlinx.serialization.protobuf

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

@Deprecated("A workaround Nothing serializer doesn't work for the JS target.")
@ExperimentalSerializationApi
expect val extendedProtoBuf: ProtoBuf
