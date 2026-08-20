# AegisAuth Wiki - Configuration

AegisAuth stores its configuration settings inside the `config.yml` file. This guide provides a detailed explanation of each block.

---

## Complete config.yml Reference

Here is the default structure of the `config.yml` file:

```yaml
# Global display language ("vi" for Vietnamese, "en" for English)
language: "vi"

# Database Connection Settings
database:
  # Choose SQLITE for local file-based database or MYSQL for external DB
  type: "SQLITE"
  sqlite:
    file: "auth_database.db"
  mysql:
    host: "localhost"
    port: 3306
    database: "minecraft_auth"
    username: "root"
    password: ""
    use-ssl: false

# Security Parameters
security:
  # Argon2id Cryptographic Settings (Tweak for hardware performance)
  argon2:
    iterations: 3
    memory-kb: 65536  # 64 MB
    parallelism: 1
  password:
    min-length: 6
    max-length: 32

# Session Persistence Settings
session:
  enabled: true
  ttl-minutes: 720    # 12 hours

# Brute-force Prevention Settings
rate-limit:
  enabled: true
  max-failed-attempts: 5
  lockout-duration-minutes: 60  # 1 hour

# Timeout countdowns
auth-timeout:
  seconds: 60
  reminder-interval-seconds: 10
```

---

## Detailed Block Descriptions

### 1. Database Block
- **type**: Supported options are `SQLITE` and `MYSQL`.
- **mysql.use-ssl**: Should be set to `true` if your MySQL provider requires encrypted connections.

> [!WARNING]
> If you are using MySQL, never share your `config.yml` file publicly as it contains plain-text credentials for your database.

### 2. Argon2 Block
Argon2id is memory-hard. Adjust these values depending on your server's available RAM:
- **memory-kb**: High memory usage prevents CPU/GPU cracking, but allocating too much RAM (e.g. 512MB+) may cause JVM out-of-memory errors on cheap hostings. 64MB (65536 KB) is the recommended sweet spot.

### 3. Session Block
- **session.enabled**: If set to `true`, players who disconnect and reconnect within the TTL duration from the exact same IP address will be logged in automatically without prompting for a password.
