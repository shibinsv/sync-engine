# Assumptions

This document records ambiguities encountered in the specification and the interpretations applied during implementation.

---

## clock_skew_tolerance_seconds

**Ambiguity**

The specification includes `clock_skew_tolerance_seconds` but does not explicitly define how it should be used during conflict resolution.

**Interpretation**

When the timestamp difference between competing local and remote events is less than or equal to the configured tolerance, the events are treated as concurrent updates and resolved using the configured `tie_breaker`.

---

## tombstone_ttl_seconds

**Ambiguity**

The specification defines `tombstone_ttl_seconds` but does not specify the reference time used to evaluate tombstone expiry.

**Interpretation**

A tombstone (`deleted=true`) is considered expired when its age relative to the latest event timestamp in the merged event set exceeds the configured TTL.

---

## last_modified tie handling

**Ambiguity**

Multiple winning field events may share the same timestamp.

**Interpretation**

When timestamps are equal, the lexicographically greater `event_id` is used to determine `last_modified` and `last_source`.

---

## resolution_reason values

**Ambiguity**

The specification requires a `resolution_reason` field but does not define allowed values.

**Interpretation**

The implementation emits descriptive deterministic values such as:

* `local_timestamp_later`
* `remote_timestamp_later`
* `exact_timestamp_tie_tie_breaker_remote_wins`
* `clock_skew_within_tolerance_tie_breaker_remote_wins`

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

This preserves entity counts and deterministic output structure.
