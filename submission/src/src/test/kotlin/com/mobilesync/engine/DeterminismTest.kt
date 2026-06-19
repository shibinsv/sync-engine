package com.mobilesync.engine

import com.mobilesync.io.CaseFileWriter
import com.mobilesync.model.ResolvedEntity
import com.mobilesync.model.SyncCaseResult
import com.mobilesync.model.SyncReport
import kotlin.test.Test
import kotlin.test.assertEquals

class DeterminismTest {
    @Test
    fun repeatedWritesProduceIdenticalBytes() {
        val writer = CaseFileWriter()
        val result = SyncCaseResult(
            resolvedState = listOf(
                ResolvedEntity(
                    entityId = "contact-002",
                    fields = mapOf("name" to "Bob"),
                    deleted = false,
                    lastModified = "2024-01-15T10:01:00Z",
                    lastSource = "local",
                ),
                ResolvedEntity(
                    entityId = "contact-001",
                    fields = mapOf("phone" to "+1"),
                    deleted = false,
                    lastModified = "2024-01-15T10:00:05Z",
                    lastSource = "local",
                ),
            ),
            conflicts = emptyList(),
            report = SyncReport(
                totalEntities = 2,
                entitiesSynced = 2,
                entitiesDeleted = 0,
                totalConflicts = 0,
                conflictsResolvedLocal = 0,
                conflictsResolvedRemote = 0,
                localEventsProcessed = 2,
                remoteEventsProcessed = 0,
                duplicateEventsDropped = 0,
                entitiesWithConflicts = 0,
            ),
        )

        val firstDir = kotlin.io.path.createTempDirectory("sync-first")
        val secondDir = kotlin.io.path.createTempDirectory("sync-second")

        writer.writeCaseOutputs(firstDir, result)
        writer.writeCaseOutputs(secondDir, result)

        listOf("resolved_state.json", "sync_conflicts.json", "sync_report.json").forEach { fileName ->
            val first = firstDir.resolve(fileName).toFile().readBytes()
            val second = secondDir.resolve(fileName).toFile().readBytes()
            assertEquals(first.size, second.size)
            assertEquals(first.toList(), second.toList())
        }
    }
}
