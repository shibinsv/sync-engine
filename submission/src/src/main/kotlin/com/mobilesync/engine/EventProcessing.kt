package com.mobilesync.engine

import com.mobilesync.model.EventSource
import com.mobilesync.model.ParsedEvent
import com.mobilesync.model.ParsedRules
import com.mobilesync.model.SyncEvent
import com.mobilesync.model.TieBreaker
import java.time.Duration
import java.time.Instant

object EventParser {
    fun parse(event: SyncEvent): ParsedEvent =
        ParsedEvent(
            event = event,
            source = EventSource.fromRaw(event.source),
            instant = Instant.parse(event.timestamp),
        )
}

object EventDeduplicator {
    fun deduplicate(
        localEvents: List<SyncEvent>,
        remoteEvents: List<SyncEvent>,
    ): Pair<List<SyncEvent>, Int> {
        val merged = LinkedHashMap<String, SyncEvent>()
        localEvents.forEach { merged[it.eventId] = it }

        var duplicatesDropped = 0
        remoteEvents.forEach { event ->
            if (merged.containsKey(event.eventId)) {
                duplicatesDropped++
            }
            merged[event.eventId] = event
        }

        return merged.values.toList() to duplicatesDropped
    }
}

object TombstoneFilter {
    fun filterExpired(events: List<ParsedEvent>, rules: ParsedRules): List<ParsedEvent> {
        if (events.isEmpty()) {
            return events
        }

        val syncAnchor = events.maxOf { it.instant }
        return events.filter { parsed ->
            val event = parsed.event
            if (event.field != "deleted" || event.value != "true") {
                true
            } else {
                val ageSeconds = Duration.between(parsed.instant, syncAnchor).seconds
                ageSeconds <= rules.tombstoneTtlSeconds
            }
        }
    }
}

object EventSelector {
    private val bestEventComparator = compareBy<ParsedEvent>({ it.instant }).thenBy { it.event.eventId }

    fun bestEvent(events: List<ParsedEvent>): ParsedEvent? =
        events.maxWithOrNull(bestEventComparator)
}

object EventComparator {
    fun compareForLastModified(a: ParsedEvent, b: ParsedEvent): Int {
        val timeCompare = a.instant.compareTo(b.instant)
        return if (timeCompare != 0) {
            timeCompare
        } else {
            a.event.eventId.compareTo(b.event.eventId)
        }
    }

    fun resolveWinner(
        localBest: ParsedEvent,
        remoteBest: ParsedEvent,
        rules: ParsedRules,
    ): Pair<ParsedEvent, String> =
        when (rules.conflictStrategy) {
            com.mobilesync.model.ConflictStrategy.LocalWins ->
                localBest to "conflict_strategy_local_wins"
            com.mobilesync.model.ConflictStrategy.RemoteWins ->
                remoteBest to "conflict_strategy_remote_wins"
            com.mobilesync.model.ConflictStrategy.LastWriteWins ->
                resolveLastWriteWins(localBest, remoteBest, rules)
        }

    private fun resolveLastWriteWins(
        localBest: ParsedEvent,
        remoteBest: ParsedEvent,
        rules: ParsedRules,
    ): Pair<ParsedEvent, String> {
//        val skewSeconds = kotlin.math.abs(localBest.instant.epochSecond - remoteBest.instant.epochSecond)
//
//        if (skewSeconds <= rules.clockSkewToleranceSeconds) {
//            return applyTieBreaker(localBest, remoteBest, rules, "clock_skew_within_tolerance")
//        }

        return when {
            localBest.instant.isAfter(remoteBest.instant) ->
                localBest to "local_timestamp_later"

            remoteBest.instant.isAfter(localBest.instant) ->
                remoteBest to "remote_timestamp_later"

            else ->
                applyTieBreaker(
                    localBest,
                    remoteBest,
                    rules,
                    "exact_timestamp_tie"
                )
        }
    }

    private fun applyTieBreaker(
        localBest: ParsedEvent,
        remoteBest: ParsedEvent,
        rules: ParsedRules,
        tieContext: String,
    ): Pair<ParsedEvent, String> {
        return when (rules.tieBreaker) {
            TieBreaker.RemoteWins ->
                remoteBest to "${tieContext}_tie_breaker_remote_wins"
            TieBreaker.LocalWins ->
                localBest to "${tieContext}_tie_breaker_local_wins"
        }
    }
}
