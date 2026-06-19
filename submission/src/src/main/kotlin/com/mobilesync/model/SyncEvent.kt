package com.mobilesync.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncEvent(
    @SerialName("event_id") val eventId: String,
    @SerialName("entity_id") val entityId: String,
    val field: String,
    val value: String,
    val timestamp: String,
    val source: String,
    @SerialName("device_id") val deviceId: String,
    val seq: Int,
)

@Serializable
data class SyncRules(
    @SerialName("conflict_strategy") val conflictStrategy: String,
    @SerialName("tie_breaker") val tieBreaker: String,
    @SerialName("tombstone_ttl_seconds") val tombstoneTtlSeconds: Long,
    @SerialName("clock_skew_tolerance_seconds") val clockSkewToleranceSeconds: Long,
)

enum class EventSource(val raw: String) {
    Local("local"),
    Remote("remote"),
    ;

    companion object {
        fun fromRaw(raw: String): EventSource =
            entries.firstOrNull { it.raw == raw }
                ?: error("Unknown event source: $raw")
    }
}

enum class ConflictStrategy(val raw: String) {
    LastWriteWins("last_write_wins"),
    RemoteWins("remote_wins"),
    LocalWins("local_wins"),
    ;

    companion object {
        fun fromRaw(raw: String): ConflictStrategy =
            entries.firstOrNull { it.raw == raw }
                ?: error("Unknown conflict strategy: $raw")
    }
}

enum class TieBreaker(val raw: String) {
    RemoteWins("remote_wins"),
    LocalWins("local_wins"),
    ;

    companion object {
        fun fromRaw(raw: String): TieBreaker =
            entries.firstOrNull { it.raw == raw }
                ?: error("Unknown tie breaker: $raw")
    }
}

enum class SyncField(val raw: String) {
    Name("name"),
    Phone("phone"),
    Email("email"),
    Address("address"),
    Deleted("deleted"),
    ;

    companion object {
        val allowed = entries.map { it.raw }.toSet()

        fun fromRaw(raw: String): SyncField =
            entries.firstOrNull { it.raw == raw }
                ?: error("Unknown field: $raw")
    }
}
