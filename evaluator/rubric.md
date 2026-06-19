# Rubric — Mobile Sync Engine Assessment (100 pts)

This rubric is shared with candidates in full. No hidden criteria.

---

## Automated Scoring — 50 pts

Scored by `evaluator/evaluate.py` against `golden_outputs/`.

| Output file              | Pts | Metric |
|--------------------------|----:|--------|
| `resolved_state.json`    | 20  | 80% field-level correctness (field values + deleted flag per entity) + 20% ordering (first 20 entity_ids match golden) |
| `sync_conflicts.json`    | 20  | 70% F1 on (entity_id, field) conflict pairs + 30% resolution accuracy (`local_wins` vs `remote_wins`) |
| `sync_report.json`       | 10  | 1 pt per correct numeric field (10 fields, exact integer match) |

**Per-case weights:**

| Case                  | Weight | Purpose |
|-----------------------|-------:|---------|
| case-01-small         | 10%    | Sanity — does the code run at all? |
| case-02-conflicts     | 25%    | Dedup + conflict resolution |
| case-03-edge          | 30%    | Boundary correctness (ties, tombstones, empty values) |
| case-04-scale         | 35%    | Generalization across 200 entities |

---

## Manual Scoring — 50 pts

Scored by one reviewer. Scores are published with a one-line justification per row.

### A. Code Architecture (15 pts)

| Sub-item | Pts |
|----------|----:|
| Layered design: parse → resolve → emit are distinct units; I/O not tangled with algorithm | 5 |
| Algorithm matches spec exactly: dedup-by-event_id → group-by-(entity,field) → pick-winner → build outputs | 5 |
| Deterministic: same input produces byte-identical outputs across runs (no unordered maps in output path, no system-clock reads) | 3 |
| Defensive programming: null/missing field guards, empty-array handling, meaningful error messages on bad input | 2 |

### B. Mobile-Specific Practices (14 pts)

| Sub-item | Pts |
|----------|----:|
| Structured concurrency: sync work runs off the main thread (Kotlin coroutines / Swift async-await or OperationQueue) | 4 |
| Thread safety: no data races; shared state is protected (Dispatchers, actors, `@MainActor`, serialized queue, or similar) | 4 |
| Memory efficiency: does not load all events for all four cases simultaneously; processes one case at a time | 3 |
| Idiomatic language use: Kotlin data classes + sealed classes + `when`; Swift `Codable` + `Result` + `guard let`; no raw `Map<String, Any>`, no force-unwraps in production code paths | 3 |

### C. Testing & Verification (10 pts)

| Sub-item | Pts |
|----------|----:|
| Unit tests with meaningful assertions: tests exist, cover individual components, use specific `assert`/`assertEquals` calls — not trivial stubs | 4 |
| Scenario coverage: tests cover ≥ 3 of: no-conflict, remote-wins conflict, local-wins conflict, exact-tie tie_breaker, tombstone | 4 |
| Determinism verification: evidence that repeated runs produce identical output (test, diff script, or documented confirmation) | 2 |

### D. AI Usage Log (6 pts)

| Sub-item | Pts |
|----------|----:|
| Transparency: AI_USAGE.md names the tool, lists prompts or prompt summaries, and identifies what was AI-generated vs hand-written | 2 |
| Architectural decisions documented: explains **why** key design choices were made (not just what — the reasoning behind them) | 2 |
| Independent validation: describes how AI-generated code was verified (specific functions tested, outputs compared, edge cases traced manually) | 2 |

### E. Documentation (5 pts)

| Sub-item | Pts |
|----------|----:|
| `README.md` — exact run command + language version + dependency manager (Gradle/SPM lock file present) | 2 |
| `assumptions.md` — lists ≥ 2 specific ambiguities encountered and how each was resolved | 2 |
| `RUN_RESULTS.md` — wall-clock time and file sizes per case **plus** at least one observation, trade-off, or area for improvement | 1 |

---

## Level Bands

| Band    | Total | Signal |
|---------|------:|--------|
| IC      | ≥ 65  | Correct algorithm + basic code quality + README |
| Senior  | ≥ 80  | Above + thread safety, idiomatic language patterns, meaningful test coverage |
| Staff+  | ≥ 90  | Above + memory efficiency, full edge-case coverage, determinism verification, thorough AI documentation |

---

## Fairness Rules (applied uniformly during scoring)

1. **Credit content over format.** Extra JSON fields → format deduction only, not correctness deduction.
2. **Partial credit is the default.** Correct dedup but wrong conflict resolution → full dedup credit, partial conflict credit.
3. **No penalty for choices outside the spec.** Log format, build tool, internal folder structure — don't deduct.
4. **Tests count even if incomplete.** One passing test with a real assertion > zero tests > broken test harness.
5. **Force-unwraps in test code are not penalised.** Only production code paths are evaluated for B4.
