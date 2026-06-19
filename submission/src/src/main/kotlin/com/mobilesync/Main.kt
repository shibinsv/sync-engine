package com.mobilesync

import com.mobilesync.io.SyncCaseRunner
import kotlinx.coroutines.runBlocking
import kotlin.io.path.Path

fun main(args: Array<String>) = runBlocking {
    val parsedArgs = parseArgs(args)
    val datasetDir = Path(parsedArgs.dataset)
    val outputDir = Path(parsedArgs.output)

    val runner = SyncCaseRunner()
    val metrics = runner.runAllCases(datasetDir, outputDir)

    println("Sync completed for ${metrics.size} case(s).")
    metrics.forEach { metric ->
        println(
            "${metric.caseId}: ${metric.wallClockMs}ms | " +
                "resolved=${metric.resolvedStateBytes}B, " +
                "conflicts=${metric.conflictsBytes}B, " +
                "report=${metric.reportBytes}B",
        )
    }
}

private data class CliArgs(
    val dataset: String,
    val output: String,
)

private fun parseArgs(args: Array<String>): CliArgs {
    var dataset: String? = null
    var output: String? = null

    var index = 0
    while (index < args.size) {
        when (args[index]) {
            "--dataset" -> {
                dataset = args.getOrNull(index + 1)
                    ?: error("Missing value for --dataset")
                index += 2
            }
            "--output" -> {
                output = args.getOrNull(index + 1)
                    ?: error("Missing value for --output")
                index += 2
            }
            else -> error("Unknown argument: ${args[index]}")
        }
    }

    return CliArgs(
        dataset = dataset ?: error("--dataset is required"),
        output = output ?: error("--output is required"),
    )
}
