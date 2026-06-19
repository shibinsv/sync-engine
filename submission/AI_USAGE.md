# AI Usage Log

## Tools Used

* ChatGPT
* Cursor (AI Coding Assistant)

## Requirements Analysis

### Tool

ChatGPT

### Prompt

> Explain the synchronization assignment in simple terms. What is expected, how should conflicts be resolved, and what should the generated outputs contain?

### Outcome

* Broke down the assignment requirements.
* Explained synchronization workflow and expected outputs.
* Identified important concepts such as conflicts, tombstones, duplicates, and reporting.

---

### Tool

ChatGPT

### Prompt

> Review the provided datasets and explain the scenarios being tested in each case.

### Outcome

* Explained the purpose of each dataset.
* Identified conflict scenarios, duplicate events, tombstones, and edge cases.

---

## Implementation Planning

### Tool

Cursor

### Prompt

> Review assessment requirements and split implementation into phased plan for Kotlin.

### Outcome

* Phased architecture plan:

   * Parse
   * Deduplicate
   * Group
   * Resolve
   * Emit
* Proposed project layout.
* Identified implementation risks and validation points.

---

## Implementation

### Tool

Cursor

### Prompt

> Provide the whole project in a runnable way to verify against inputs.

### Outcome

* Generated Gradle Kotlin JVM project.
* Generated sync engine implementation.
* Generated CLI runner.
* Generated tests.
* Generated output writers.

---

## Validation and Review

### Tool

ChatGPT

### Prompt

> Given these local events, remote events, and sync rules, what should the expected output be?

### Outcome

* Reviewed expected conflict counts.
* Reviewed deleted entity handling.
* Reviewed duplicate handling.
* Reviewed report calculations.

---

### Tool

ChatGPT

### Prompt

> Validate whether all scenarios are covered according to the assignment requirements and identify any additional edge cases.

### Outcome

* Reviewed implementation behavior.
* Discussed timestamp ties.
* Discussed clock skew handling.
* Discussed tombstone handling.
* Discussed deterministic output behavior.

---

### Tool

ChatGPT

### Prompt

> Review the generated reports and conflict outputs and verify whether they are logically correct according to the specification.

### Outcome

* Reviewed Case-01 through Case-04 outputs.
* Verified conflict counts.
* Verified report consistency.
* Reviewed assumptions and edge cases.

---

## Final Review

The final outputs, assumptions, and documentation were manually reviewed before submission.

AI tools were used to assist with understanding requirements, implementation, validation, and documentation. Final implementation decisions and output verification were performed manually.
