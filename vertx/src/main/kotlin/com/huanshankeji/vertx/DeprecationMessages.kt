package com.huanshankeji.vertx

// TODO consider either removing or migrating APIs marked with this
internal const val CALLBACK_MODEL_DEPRECATION_MESSAGE =
    "Callback model APIs using `Promise` in their signatures shall be deprecated as those will be deprecated in Vert.x. " +
            "See https://vertx.io/docs/guides/vertx-5-migration-guide/#_embracing_the_future_model ." +
            "These APIs will either be removed or migrated to the future model."
