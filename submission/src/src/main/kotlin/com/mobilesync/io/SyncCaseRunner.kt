package com.mobilesync.io

import com.mobilesync.engine.SyncEngine
import com.mobilesync.model.SyncCaseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.system.measureTimeMillis

data class CaseRunMetrics(
    val caseId: String,
    val wallClockMs: Long,
    val resolvedStateBytes: Long,
    val conflictsBytes: Long,
    val reportBytes: Long,
)

class SyncCaseRunner(
    private val engine: SyncEngine = SyncEngine(),
    private val reader: CaseFileReader = CaseFileReader(),
    private val writer: CaseFileWriter = CaseFileWriter(),
) {
    suspend fun runCase(datasetDir: Path, caseId: String, outputDir: Path): Pair<SyncCaseResult, CaseRunMetrics> =
        withContext(Dispatchers.Default) {
            val caseDir = datasetDir.resolve(caseId)
            val localEvents = reader.readEvents(caseDir.resolve("local_events.json"))
            val remoteEvents = reader.readEvents(caseDir.resolve("remote_events.json"))
            val rules = reader.readRules(caseDir.resolve("sync_rules.json"))

            var result: SyncCaseResult? = null
            val elapsed = measureTimeMillis {
                result = engine.sync(localEvents, remoteEvents, rules)
            }

            val caseOutputDir = outputDir.resolve(caseId)
            writer.writeCaseOutputs(caseOutputDir, result!!)

            val metrics = CaseRunMetrics(
                caseId = caseId,
                wallClockMs = elapsed,
                resolvedStateBytes = caseOutputDir.resolve("resolved_state.json").toFile().length(),
                conflictsBytes = caseOutputDir.resolve("sync_conflicts.json").toFile().length(),
                reportBytes = caseOutputDir.resolve("sync_report.json").toFile().length(),
            )
            result!! to metrics
        }

    suspend fun runAllCases(datasetDir: Path, outputDir: Path): List<CaseRunMetrics> {
        val caseIds = datasetDir.listDirectoryEntries()
            .filter { it.isDirectory() }
            .map { it.name }
            .sorted()

        val metrics = mutableListOf<CaseRunMetrics>()
        for (caseId in caseIds) {
            val (_, caseMetrics) = runCase(datasetDir, caseId, outputDir)
            metrics += caseMetrics
        }
        return metrics
    }
}
