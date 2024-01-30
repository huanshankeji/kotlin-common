package com.huanshankeji.kotlinx.serialization.benchmark

import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup

@State(Scope.Benchmark)
@Warmup(1)
@Measurement(1)
abstract class BaseBenchmark