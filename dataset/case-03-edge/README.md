# case-03-edge

20 entities: exact timestamp ties (tie_breaker applies), tombstone+resurrection (contact-011), empty string field (contact-012), multiple local events per field (latest wins, contacts 016–020).

## Stats

| Field | Value |
|-------|-------|
| Entities | 20 |
| Local events | 38 |
| Remote events | 19 |

## Adversarial Patterns

exact timestamp ties, tombstone resurrection, empty string value, multiple events per (entity_id, field)

## Sync Rules

| Parameter | Value |
|-----------|-------|
| conflict_strategy | last_write_wins |
| tie_breaker | remote_wins |
| tombstone_ttl_seconds | 86400 |
| clock_skew_tolerance_seconds | 30 |
