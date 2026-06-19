package com.mobilesync.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResolvedEntity(
    @SerialName("entity_id") val entityId: String,
    val fields: Map<String, String>,
    val deleted: Boolean,
    @SerialName("last_modified") val lastModified: String?,
    @SerialName("last_source") val lastSource: String?,
)

@Serializable
data class SyncConflict(
    @SerialName("entity_id") val entityId: String,
    val field: String,
    @SerialName("local_value") val localValue: String,
    @SerialName("remote_value") val remoteValue: String,
    @SerialName("local_timestamp") val localTimestamp: String,
    @SerialName("remote_timestamp") val remoteTimestamp: String,
    val resolution: String,
    @SerialName("resolution_reason") val resolutionReason: String,
)

@Serializable
data class SyncReport(
    @SerialName("total_entities") val totalEntities: Int,
    @SerialName("entities_synced") val entitiesSynced: Int,
    @SerialName("entities_deleted") val entitiesDeleted: Int,
    @SerialName("total_conflicts") val totalConflicts: Int,
    @SerialName("conflicts_resolved_local") val conflictsResolvedLocal: Int,
    @SerialName("conflicts_resolved_remote") val conflictsResolvedRemote: Int,
    @SerialName("local_events_processed") val localEventsProcessed: Int,
    @SerialName("remote_events_processed") val remoteEventsProcessed: Int,
    @SerialName("duplicate_events_dropped") val duplicateEventsDropped: Int,
    @SerialName("entities_with_conflicts") val entitiesWithConflicts: Int,
)

data class SyncCaseResult(
    val resolvedState: List<ResolvedEntity>,
    val conflicts: List<SyncConflict>,
    val report: SyncReport,
)

sealed class SyncOutcome {
    data class Success(val result: SyncCaseResult) : SyncOutcome()
    data class Failure(val message: String) : SyncOutcome()
}

sealed class ConflictResolution {
    abstract val winner: ParsedEvent
    abstract val resolution: String
    abstract val reason: String

    data class LocalWins(
        override val winner: ParsedEvent,
        override val reason: String,
    ) : ConflictResolution() {
        override val resolution: String = "local_wins"
    }

    data class RemoteWins(
        override val winner: ParsedEvent,
        override val reason: String,
    ) : ConflictResolution() {
        override val resolution: String = "remote_wins"
    }
}

data class ParsedEvent(
    val event: SyncEvent,
    val source: EventSource,
    val instant: java.time.Instant,
)

data class ParsedRules(
    val rules: SyncRules,
    val conflictStrategy: ConflictStrategy,
    val tieBreaker: TieBreaker,
    val tombstoneTtlSeconds: Long,
    val clockSkewToleranceSeconds: Long,
)
