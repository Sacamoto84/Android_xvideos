package com.client.xvideos.feature.redgifs

enum class Order(val value: String) {
    TRENDING("trending"),
    TOP("top"),
    LATEST("latest"),
    OLDEST("oldest"),
    RECENT("recent"),
    BEST("best"),
    TOP28("top28"),
    NEW("new");

    companion object {
        private val deprecatedNames = setOf(
            "trending", "top", "latest", "oldest", "recent", "best", "top28", "new"
        )

        fun from(name: String): Order? {
            val upperCaseName = name.uppercase()
            return try {
                if (name in deprecatedNames && name != upperCaseName) {
                    @Suppress("DEPRECATION")
                    println("WARNING: 'Order.$name' is deprecated, use 'Order.${upperCaseName}' instead.")
                }
                valueOf(upperCaseName)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}

enum class MediaType(val value: String) {
    IMAGE("i"),
    GIF("g");
}











