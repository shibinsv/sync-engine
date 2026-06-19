package com.mobilesync.engine

import com.mobilesync.model.ConflictStrategy
import com.mobilesync.model.EventSource
import com.mobilesync.model.ParsedEvent
import com.mobilesync.model.ParsedRules
import com.mobilesync.model.ResolvedEntity
import com.mobilesync.model.SyncCaseResult
import com.mobilesync.model.SyncConflict
import com.mobilesync.model.SyncEvent
import com.mobilesync.model.SyncReport
import com.mobilesync.model.SyncRules
import com.mobilesync.model.TieBreaker

class SyncEngine {
    fun sync(
        localEvents: List<SyncEvent>,
        remoteEvents: List<SyncEvent>,
        syncRules: SyncRules,
    ): SyncCaseResult {
        val rules = parseRules(syncRules)
        val (mergedEvents, duplicateEventsDropped) =
            EventDeduplicator.deduplicate(localEvents, remoteEvents)

        val parsedEvents = mergedEvents
            .map(EventParser::parse)
            .let { TombstoneFilter.filterExpired(it, rules) }

        val allEntityIds = buildSet {
            localEvents.forEach { add(it.entityId) }
            remoteEvents.forEach { add(it.entityId) }
        }

        val grouped = parsedEvents.groupBy { it.event.entityId to it.event.field }
        val winnersByEntityField = linkedMapOf<Pair<String, String>, ParsedEvent>()
        val conflicts = mutableListOf<SyncConflict>()

        grouped.entries
            .sortedWith(compareBy({ it.key.first }, { it.key.second }))
            .forEach { (key, eventsInGroup) ->
                val (entityId, field) = key
                val localEventsInGroup = eventsInGroup.filter { it.source == EventSource.Local }
                val remoteEventsInGroup = eventsInGroup.filter { it.source == EventSource.Remote }

                val bestLocal = EventSelector.bestEvent(localEventsInGroup)
                val bestRemote = EventSelector.bestEvent(remoteEventsInGroup)

                when {
                    bestLocal != null && bestRemote != null -> {
                        val (winner, reason) = EventComparator.resolveWinner(bestLocal, bestRemote, rules)
                        winnersByEntityField[key] = winner
                        conflicts += SyncConflict(
                            entityId = entityId,
                            field = field,
                            localValue = bestLocal.event.value,
                            remoteValue = bestRemote.event.value,
                            localTimestamp = bestLocal.event.timestamp,
                            remoteTimestamp = bestRemote.event.timestamp,
                            resolution = if (winner.source == EventSource.Local) "local_wins" else "remote_wins",
                            resolutionReason = reason,
                        )
                    }
                    bestLocal != null -> winnersByEntityField[key] = bestLocal
                    bestRemote != null -> winnersByEntityField[key] = bestRemote
                }
            }

        val resolvedState = allEntityIds
            .sorted()
            .map { entityId -> buildResolvedEntity(entityId, winnersByEntityField) }

        val conflictsSorted = conflicts.sortedWith(compareBy({ it.entityId }, { it.field }))
        val entitiesDeleted = resolvedState.count { it.deleted }
        val entitiesWithConflicts = conflictsSorted.map { it.entityId }.distinct().size

        val report = SyncReport(
            totalEntities = allEntityIds.size,
            entitiesSynced = allEntityIds.size - entitiesDeleted,
            entitiesDeleted = entitiesDeleted,
            totalConflicts = conflictsSorted.size,
            conflictsResolvedLocal = conflictsSorted.count { it.resolution == "local_wins" },
            conflictsResolvedRemote = conflictsSorted.count { it.resolution == "remote_wins" },
            localEventsProcessed = localEvents.size,
            remoteEventsProcessed = remoteEvents.size,
            duplicateEventsDropped = duplicateEventsDropped,
            entitiesWithConflicts = entitiesWithConflicts,
        )

        return SyncCaseResult(
            resolvedState = resolvedState,
            conflicts = conflictsSorted,
            report = report,
        )
    }

    private fun parseRules(syncRules: SyncRules): ParsedRules =
        ParsedRules(
            rules = syncRules,
            conflictStrategy = ConflictStrategy.fromRaw(syncRules.conflictStrategy),
            tieBreaker = TieBreaker.fromRaw(syncRules.tieBreaker),
            tombstoneTtlSeconds = syncRules.tombstoneTtlSeconds,
            clockSkewToleranceSeconds = syncRules.clockSkewToleranceSeconds,
        )

    private fun buildResolvedEntity(
        entityId: String,
        winnersByEntityField: Map<Pair<String, String>, ParsedEvent>,
    ): ResolvedEntity {
        val fieldWinners = winnersByEntityField
            .filterKeys { (winnerEntityId, _) -> winnerEntityId == entityId }
            .entries
            .sortedBy { it.key.second }

        val fields = linkedMapOf<String, String>()
        var deleted = false
        var latestWinner: ParsedEvent? = null

        fieldWinners.forEach { (_, winner) ->
            if (winner.event.field == "deleted") {
                deleted = winner.event.value == "true"
            } else {
                fields[winner.event.field] = winner.event.value
            }

            latestWinner = when (val current = latestWinner) {
                null -> winner
                else ->
                    if (EventComparator.compareForLastModified(winner, current) > 0) winner else current
            }
        }

        return ResolvedEntity(
            entityId = entityId,
            fields = fields,
            deleted = deleted,
            lastModified = latestWinner?.event?.timestamp,
            lastSource = latestWinner?.source?.raw,
        )
    }
}
