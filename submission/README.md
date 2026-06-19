# Submission README

**Candidate:** Shibin
**Date:** 2026-06-19
**Language:** Kotlin
**Language Version:** Kotlin 1.9.24 (JVM 17)

## Prerequisites

* JDK 17+
* Gradle Wrapper (included)
* Dataset folder provided with the assessment

Expected structure:

```text
assessment_package/
├── dataset/
└── submission/
    ├── src/
    └── outputs/
```

## Verify Setup Before Running

Navigate to the Gradle project:

```bash
cd submission/src
```

Verify the dataset exists:

```bash
ls ../../dataset
```

Clean previous outputs:

```bash
rm -rf ../outputs/*
```

Verify the project compiles successfully:

```bash
./gradlew clean build
```

Run tests:

```bash
./gradlew test
```

## Run the Sync Engine

From the Gradle project directory:

```bash
./gradlew run --args="--dataset ../../dataset --output ../outputs"
```

Alternative task (if configured):

```bash
./gradlew runSync
```

## Validate Generated Outputs

List generated files:

```bash
find ../outputs -type f
```

Expected structure:

```text
outputs/
├── case-01-small/
│   ├── resolved_state.json
│   ├── sync_conflicts.json
│   └── sync_report.json
├── case-02-conflicts/
├── case-03-edge/
└── case-04-scale/
```

Verify files contain valid JSON:

```bash
cat ../outputs/case-01-small/sync_report.json
```

```bash
cat ../outputs/case-01-small/resolved_state.json
```

```bash
cat ../outputs/case-01-small/sync_conflicts.json
```

## Verification

The implementation was verified using all provided datasets.

Validation included:

* Successful project compilation
* Successful test execution
* Successful processing of all provided cases
* Verification that output files were generated for each dataset
* Repeated execution to confirm deterministic results
* Manual review of generated outputs

The following behaviors were specifically reviewed:

* Conflict resolution
* Duplicate event handling
* Tombstone processing
* Tombstone expiry and resurrection scenarios
* Entity reconstruction
* Report generation
* Deterministic output generation
* Timestamp tie handling
* Multi-update field scenarios
* Local-only and remote-only entities
* Edge-case scenarios

Additional implementation assumptions are documented in `assumptions.md`.


## Dependencies

| Dependency                 | Version | Purpose                        |
| -------------------------- | ------- | ------------------------------ |
| kotlinx-serialization-json | 1.6.3   | JSON parsing and serialization |
| kotlinx-coroutines-core    | 1.8.1   | Coroutine support              |

## Project Layout

```text
src/main/kotlin/com/mobilesync/
```

Contains:

* Sync engine
* Conflict resolution logic
* Dataset processing
* Report generation

Generated outputs:

```text
../outputs/
```

Input datasets:

```text
../../dataset/
```

## Output Sizes

| Case              | resolved_state.json | sync_conflicts.json | sync_report.json |
| ----------------- | ------------------: | ------------------: | ---------------: |
| case-01-small     |             2,037 B |                 3 B |            298 B |
| case-02-conflicts |             6,350 B |             6,793 B |            301 B |
| case-03-edge      |             4,270 B |             2,007 B |            298 B |
| case-04-scale     |            42,535 B |            54,024 B |            308 B |

See `RUN_RESULTS.md` for execution timings.

## Development Notes

The implementation was developed and validated iteratively using the provided specification and datasets.

Particular attention was given to conflict resolution, duplicate event handling, tombstone processing, deterministic output generation, and edge-case behavior.

Any specification ambiguities encountered during development are documented in `assumptions.md`.
