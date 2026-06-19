# AI Usage Log

## Tools Used

* ChatGPT
* Cursor (AI Coding Assistant)

---

## Requirements Analysis

### Tool

ChatGPT

### Prompt

> Explain the synchronization assignment in simple terms. What is expected, how should conflicts be resolved, and what should the generated outputs contain?

### Outcome

* Broke down the assignment requirements.
* Explained synchronization workflow and expected outputs.
* Identified important concepts such as conflicts, tombstones, duplicates, conflict resolution, and reporting.

---

### Tool

ChatGPT

### Prompt

> Review the provided datasets and explain the scenarios being tested in each case.

### Outcome

* Explained the purpose of each dataset.
* Identified conflict scenarios, duplicate events, tombstones, clock-skew cases, and edge cases.
* Helped understand expected behavior before implementation.

---

## Implementation Planning

### Tool

Cursor

### Prompt

> Review assessment requirements and split implementation into phased plan for Kotlin.

### Outcome

* Proposed phased architecture:

  * Parse
  * Deduplicate
  * Group
  * Resolve
  * Emit

* Suggested project structure.

* Identified implementation risks and validation areas.

---

## Implementation

### Tool

Cursor

### Prompt

> Provide the whole project in a runnable way to verify against inputs.

### Outcome

* Generated Kotlin JVM project structure.
* Generated synchronization engine implementation.
* Generated CLI runner.
* Generated JSON parsing and output handling.
* Generated unit tests.
* Generated output generation logic.

---

## Validation and Review

### Tool

ChatGPT

### Prompt

> Given these local events, remote events, and sync rules, what should the expected output be?

### Outcome

* Reviewed expected synchronization results.
* Verified conflict counts.
* Verified entity reconstruction.
* Verified report calculations.

---

### Tool

ChatGPT

### Prompt

> Validate whether all scenarios are covered according to the assignment requirements and identify any additional edge cases.

### Outcome

* Reviewed implementation behavior.
* Discussed timestamp ties.
* Discussed duplicate event handling.
* Discussed tombstone processing.
* Reviewed deterministic output behavior.
* Identified additional validation scenarios.

---

### Tool

ChatGPT

### Prompt

> Review the generated reports and conflict outputs and verify whether they are logically correct according to the specification.

### Outcome

* Reviewed outputs from Case-01 through Case-04.
* Verified conflict-resolution behavior.
* Verified report consistency.
* Reviewed assumptions and edge cases.

---

### Tool

ChatGPT

### Prompt

> Compare the implementation against the documented synchronization algorithm and identify any deviations from the specification.

### Outcome

* Reviewed synchronization flow step-by-step.
* Analyzed interpretation of `clock_skew_tolerance_seconds`.
* Compared implementation behavior against documented rules.
* Updated assumptions and implementation decisions to align with the intended synchronization algorithm.

---

## Additional Validation

### Tool

ChatGPT

### Prompt

> Generate additional validation datasets and expected outputs to verify synchronization behavior.

### Outcome

Additional custom validation scenarios were created and executed, including:

* Exact timestamp ties
* Local timestamp later
* Remote timestamp later
* Duplicate event IDs
* Multiple updates to the same field
* Tombstone wins
* Tombstone resurrection
* Tombstone TTL expiry
* Mixed field conflicts
* Deleted entities with retained fields
* Event ID tie-breaking
* Multi-entity conflict aggregation
* Empty datasets
* Local-only entities
* Remote-only entities

Generated outputs were compared against manually derived expected outputs to verify implementation correctness.

---

## Documentation

### Tool

ChatGPT

### Prompt

> Help document assumptions, validation results, and AI usage for submission.

### Outcome

* Assisted in documenting assumptions.
* Assisted in documenting validation activities.
* Assisted in preparing submission documentation.

---

## Final Review

The final implementation, generated outputs, assumptions, validation scenarios, and submission documentation were manually reviewed before submission.

AI tools were used to assist with understanding requirements, implementation planning, code generation, validation, testing, and documentation.

Final implementation decisions, assumption handling, validation of outputs, and submission review were performed manually by the candidate.
