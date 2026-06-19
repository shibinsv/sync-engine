# Mobile Sync Engine

Kotlin-based synchronization engine developed as part of a technical assessment.

## Features

* Event deduplication
* Conflict resolution (Last Write Wins)
* Tombstone handling and expiry
* Entity reconstruction
* Synchronization reporting
* Deterministic output generation

## Tech Stack

* Kotlin 1.9.24
* JVM 17
* Gradle
* Kotlin Coroutines
* Kotlin Serialization

## Run

```bash
cd submission/src

./gradlew clean build
./gradlew test

./gradlew run --args="--dataset ../../dataset --output ../outputs"
```

## Output

The engine generates:

```text
resolved_state.json
sync_conflicts.json
sync_report.json
```

for each dataset case.

## Validation

The implementation was validated using the provided datasets and additional custom scenarios covering:

* Conflict resolution
* Duplicate events
* Timestamp ties
* Tombstone handling
* Tombstone expiry
* Local-only and remote-only entities
* Multi-field conflict scenarios

## Documentation

See:

* `README.md`
* `assumptions.md`
* `AI_USAGE.md`
* `RUN_RESULTS.md`

## Author

Shibin
