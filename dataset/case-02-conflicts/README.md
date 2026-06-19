~~# case-02-conflicts

30 entities: 10 local-only, 10 remote-only, 10 with conflicts on name+phone. 4 duplicate event_ids. 2 tombstones (contacts 029, 030).

## Stats

| Field | Value |
|-------|-------|
| Entities | 30 |
| Local events | 55 |
| Remote events | 41 |

## Adversarial Patterns

duplicate event_ids (4), clock skew (±30s on phone), 2 tombstones

## Sync Rules

| Parameter | Value |
|-----------|-------|
| conflict_strategy | last_write_wins |
| tie_breaker | remote_wins |
| tombstone_ttl_seconds | 86400 |
| clock_skew_tolerance_seconds | 30 |~~
