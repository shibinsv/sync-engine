package com.mobilesync.engine

import com.mobilesync.model.SyncEvent
import com.mobilesync.model.SyncRules
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncEngineTest {
    private val engine = SyncEngine()

    private val defaultRules = SyncRules(
        conflictStrategy = "last_write_wins",
        tieBreaker = "remote_wins",
        tombstoneTtlSeconds = 86400,
        clockSkewToleranceSeconds = 30,
    )

    @Test
    fun deduplicatesDuplicateEventIdsWithRemoteWinning() {
        val local = listOf(
            event("evt-dup", "contact-001", "phone", "+1-local", "2024-01-15T10:00:00Z", "local"),
        )
        val remote = listOf(
            event("evt-dup", "contact-001", "phone", "+1-remote", "2024-01-15T10:00:00Z", "remote"),
        )

        val result = engine.sync(local, remote, defaultRules)

        assertEquals(1, result.report.duplicateEventsDropped)
        assertEquals("+1-remote", result.resolvedState.single().fields["phone"])
    }

    @Test
    fun appliesClockSkewToleranceAsTieBreaker() {
        val local = listOf(
            event("evt-local", "contact-001", "phone", "+1-local", "2024-01-15T10:01:10Z", "local"),
        )
        val remote = listOf(
            event("evt-remote", "contact-001", "phone", "+1-remote", "2024-01-15T10:01:30Z", "remote"),
        )

        val result = engine.sync(local, remote, defaultRules)
        val conflict = result.conflicts.single()

        assertEquals("remote_wins", conflict.resolution)
        assertEquals("+1-remote", result.resolvedState.single().fields["phone"])
    }

    @Test
    fun picksLatestEventWithinSingleSide() {
        val local = listOf(
            event("evt-old", "contact-001", "phone", "+1-old", "2024-01-15T10:02:40Z", "local"),
            event("evt-new", "contact-001", "phone", "+1-new", "2024-01-15T10:03:10Z", "local"),
        )

        val result = engine.sync(local, emptyList(), defaultRules)

        assertEquals("+1-new", result.resolvedState.single().fields["phone"])
        assertTrue(result.conflicts.isEmpty())
    }

    @Test
    fun resolvesExactTimestampTieWithTieBreaker() {
        val local = listOf(
            event("evt-local", "contact-001", "name", "Local Name", "2024-01-15T10:01:40Z", "local"),
        )
        val remote = listOf(
            event("evt-remote", "contact-001", "name", "Remote Name", "2024-01-15T10:01:40Z", "remote"),
        )

        val result = engine.sync(local, remote, defaultRules)

        assertEquals("Remote Name", result.resolvedState.single().fields["name"])
        assertEquals("remote_wins", result.conflicts.single().resolution)
    }

    @Test
    fun handlesTombstoneAndResurrection() {
        val local = listOf(
            event("evt-del", "contact-011", "deleted", "true", "2024-01-15T10:03:20Z", "local"),
        )
        val remote = listOf(
            event("evt-undel", "contact-011", "deleted", "false", "2024-01-15T10:04:10Z", "remote"),
            event("evt-name", "contact-011", "name", "Resurrected Contact", "2024-01-15T10:04:20Z", "remote"),
        )

        val result = engine.sync(local, remote, defaultRules)
        val entity = result.resolvedState.single { it.entityId == "contact-011" }

        assertEquals(false, entity.deleted)
        assertEquals("Resurrected Contact", entity.fields["name"])
    }

    @Test
    fun case01SmallProducesTenEntitiesAndNoConflicts() {
        val datasetRoot = datasetPath()
        val local = readEvents(datasetRoot, "case-01-small", "local_events.json")
        val remote = readEvents(datasetRoot, "case-01-small", "remote_events.json")
        val rules = readRules(datasetRoot, "case-01-small")

        val result = engine.sync(local, remote, rules)

        assertEquals(10, result.report.totalEntities)
        assertEquals(0, result.report.totalConflicts)
        assertEquals(0, result.report.duplicateEventsDropped)
    }

    private fun event(
        eventId: String,
        entityId: String,
        field: String,
        value: String,
        timestamp: String,
        source: String,
    ): SyncEvent =
        SyncEvent(
            eventId = eventId,
            entityId = entityId,
            field = field,
            value = value,
            timestamp = timestamp,
            source = source,
            deviceId = "device-001",
            seq = 0,
        )

    private fun datasetPath(): java.nio.file.Path =
        java.nio.file.Path.of("../../dataset")

    private fun readEvents(datasetRoot: java.nio.file.Path, caseId: String, fileName: String): List<SyncEvent> {
        val reader = com.mobilesync.io.CaseFileReader()
        return reader.readEvents(datasetRoot.resolve(caseId).resolve(fileName))
    }

    private fun readRules(datasetRoot: java.nio.file.Path, caseId: String): SyncRules {
        val reader = com.mobilesync.io.CaseFileReader()
        return reader.readRules(datasetRoot.resolve(caseId).resolve("sync_rules.json"))
    }
}
