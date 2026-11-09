package com.sankranthivinod.tradetracker.domain.model

enum class InstrumentType(val displayName: String) {
    OPTIONS("Options"),
    STOCKS("Stocks"),
    FUTURES("Futures"),
    CRYPTO("Crypto");

    companion object {
        fun fromDisplayName(name: String): InstrumentType {
            return entries.find { it.displayName.equals(name, ignoreCase = true) } ?: OPTIONS
        }
    }
}

enum class OptionType(val displayName: String) {
    CALL("Call"),
    PUT("Put");

    companion object {
        fun fromDisplayName(name: String?): OptionType? {
            if (name == null) return null
            return entries.find { it.displayName.equals(name, ignoreCase = true) }
        }
    }
}
