# Assumptions

This document records ambiguities encountered in the specification and the interpretations applied during implementation.

---

## clock_skew_tolerance_seconds

**Ambiguity**

The specification includes `clock_skew_tolerance_seconds` in the synchronization rules, but the conflict-resolution algorithm does not explicitly reference this value.

**Interpretation**

The implementation follows the conflict-resolution algorithm exactly as described:

* Later timestamp wins.
* Equal timestamps are resolved using the configured `tie_breaker`.

The `clock_skew_tolerance_seconds` value is parsed from the rules file but is not used during winner selection because the algorithm does not define a role for it.

**Reasoning**

The documented synchronization algorithm defines conflict resolution using timestamp ordering and tie-breakers but does not specify any behavior that modifies winner selection based on timestamp tolerance.

---

## tombstone_ttl_seconds

**Ambiguity**

The specification defines `tombstone_ttl_seconds` but does not specify the reference time used to evaluate tombstone expiry.

**Interpretation**

A tombstone (`deleted=true`) is considered expired when its age relative to the latest event timestamp in the merged event set exceeds the configured TTL.

**Reasoning**

A TTL requires a deterministic reference point. Using the latest timestamp available in the synchronization batch provides consistent and repeatable behavior.

---

## Event Selection Within a Source

**Ambiguity**

Multiple events for the same entity and field may exist within a single source.

**Interpretation**

Before conflict resolution between local and remote data, the implementation selects the most recent event for a given entity and field within each source.

When timestamps are equal, the event with the lexicographically greater `event_id` is selected.

**Reasoning**

The synchronization process requires a single candidate event from each source before applying cross-source conflict resolution. Timestamp ordering combined with deterministic `event_id` tie-breaking ensures repeatable results.

---

## last_modified tie handling

**Ambiguity**

Multiple winning field events may share the same timestamp.

**Interpretation**

When timestamps are equal, the lexicographically greater `event_id` is used to determine `last_modified` and `last_source`.

**Reasoning**

The same deterministic ordering rule used elsewhere in the implementation is applied to preserve consistency.

---

## resolution_reason values

**Ambiguity**

The specification requires a `resolution_reason` field but does not define allowed values.

**Interpretation**

The implementation emits descriptive deterministic values such as:

* `local_timestamp_later`
* `remote_timestamp_later`
* `exact_timestamp_tie_tie_breaker_remote_wins`
* `exact_timestamp_tie_tie_breaker_local_wins`
* `conflict_strategy_remote_wins`
* `conflict_strategy_local_wins`

**Reasoning**

The schema only requires a string value. Using descriptive deterministic values improves traceability and simplifies validation.

---

## Entities with no surviving field events

**Ambiguity**

The specification does not define how entities should be represented when all candidate field events are removed during filtering.

**Interpretation**

The entity remains in `resolved_state.json` with:

* Empty `fields`
* `deleted=false`
* `last_modified=null`
* `last_source=null`

**Reasoning**

Preserving the entity ensures consistency between resolved output and report-level entity counts while maintaining deterministic output structure.
