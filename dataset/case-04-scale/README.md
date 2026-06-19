# case-04-scale

200 entities (~1 000 events). Mixed patterns: local-only, remote-only, conflict (remote wins), conflict (local wins), multi-event per field. ~5% tombstone rate, ~3% duplicate events.

## Stats

| Field | Value |
|-------|-------|
| Entities | 200 |
| Local events | 381 |
| Remote events | 298 |

## Adversarial Patterns

duplicates (~3%), tombstones (~5%), conflicts (~40%), multi-event fields

## Sync Rules

| Parameter | Value |
|-----------|-------|
| conflict_strategy | last_write_wins |
| tie_breaker | remote_wins |
| tombstone_ttl_seconds | 86400 |
| clock_skew_tolerance_seconds | 30 |
