# case-01-small

Sanity check: 10 entities, no conflicts, no duplicates, no deletes.

## Stats

| Field | Value |
|-------|-------|
| Entities | 10 |
| Local events | 10 |
| Remote events | 10 |

## Adversarial Patterns

none

## Sync Rules

| Parameter | Value |
|-----------|-------|
| conflict_strategy | last_write_wins |
| tie_breaker | remote_wins |
| tombstone_ttl_seconds | 86400 |
| clock_skew_tolerance_seconds | 30 |
