# Run Results

Execution results from a local run.

Environment:

* Kotlin 1.9.24 (JVM 17)
* Gradle 8.9

Command:

```bash
./gradlew run --args="--dataset ../../dataset --output ../outputs"
```

## Timings and File Sizes

| Case              | Wall Time (ms) | resolved_state.json | sync_conflicts.json | sync_report.json |
| ----------------- | -------------: | ------------------: | ------------------: | ---------------: |
| case-01-small     |             12 |             2,037 B |                 3 B |            298 B |
| case-02-conflicts |              5 |             6,350 B |             6,793 B |            301 B |
| case-03-edge      |              2 |             4,270 B |             2,007 B |            298 B |
| case-04-scale     |             16 |            42,535 B |            54,024 B |            308 B |

## Observations

* All provided datasets were processed successfully.
* Output files were generated for every test case.
* Repeated executions produced consistent results.
* Processing completed within milliseconds for all provided datasets.
* The implementation is designed for deterministic output generation to simplify verification and review.

## Notes

Execution times may vary depending on hardware, operating system, JVM configuration, and background system activity.
