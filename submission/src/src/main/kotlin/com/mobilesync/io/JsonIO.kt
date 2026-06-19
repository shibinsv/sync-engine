package com.mobilesync.io

import com.mobilesync.model.ResolvedEntity
import com.mobilesync.model.SyncCaseResult
import com.mobilesync.model.SyncConflict
import com.mobilesync.model.SyncEvent
import com.mobilesync.model.SyncReport
import com.mobilesync.model.SyncRules
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.readText
import kotlin.io.path.writeText

object JsonConfig {
    val json: Json = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
        encodeDefaults = true
    }
}

class CaseFileReader {
    fun readEvents(path: Path): List<SyncEvent> =
        JsonConfig.json.decodeFromString<List<SyncEvent>>(path.readText())

    fun readRules(path: Path): SyncRules =
        JsonConfig.json.decodeFromString<SyncRules>(path.readText())
}

class CaseFileWriter {
    fun writeCaseOutputs(outputDir: Path, result: SyncCaseResult) {
        outputDir.createDirectories()
        writeResolvedState(outputDir.resolve("resolved_state.json"), result.resolvedState)
        writeConflicts(outputDir.resolve("sync_conflicts.json"), result.conflicts)
        writeReport(outputDir.resolve("sync_report.json"), result.report)
    }

    private fun writeResolvedState(path: Path, entities: List<ResolvedEntity>) {
        val payload = entities.map { entity ->
            buildString {
                append("  {\n")
                append("    \"entity_id\": ")
                append(JsonConfig.json.encodeToString(entity.entityId))
                append(",\n")
                append("    \"fields\": ")
                append(encodeSortedFields(entity.fields))
                append(",\n")
                append("    \"deleted\": ")
                append(entity.deleted)
                append(",\n")
                append("    \"last_modified\": ")
                append(
                    if (entity.lastModified == null) "null" else JsonConfig.json.encodeToString(entity.lastModified),
                )
                append(",\n")
                append("    \"last_source\": ")
                append(
                    if (entity.lastSource == null) "null" else JsonConfig.json.encodeToString(entity.lastSource),
                )
                append("\n")
                append("  }")
            }
        }
        path.writeText("[\n${payload.joinToString(",\n")}\n]\n")
    }

    private fun encodeSortedFields(fields: Map<String, String>): String {
        if (fields.isEmpty()) {
            return "{}"
        }
        val entries = fields.entries
            .sortedBy { it.key }
            .joinToString(", ") { (key, value) ->
                "${JsonConfig.json.encodeToString(key)}: ${JsonConfig.json.encodeToString(value)}"
            }
        return "{ $entries }"
    }

    private fun writeConflicts(path: Path, conflicts: List<SyncConflict>) {
        path.writeText(JsonConfig.json.encodeToString(conflicts) + "\n")
    }

    private fun writeReport(path: Path, report: SyncReport) {
        path.writeText(JsonConfig.json.encodeToString(report) + "\n")
    }
}
