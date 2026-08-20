# Configuration Guide

Here is the default `config.yml` with comments explaining what settings you need to adjust for your server.

```yaml
# Set language: "vi" (Vietnamese) or "en" (English)
language: "vi"

database:
  # Choose "SQLITE" (single server) or "MYSQL" (server network)
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

security:
  # Tweak Argon2id settings based on your server memory
  argon2:
    iterations: 3
    memory-kb: 65536  # Default is 64MB. Lower this if running on a tight budget server
    parallelism: 1
  password:
    min-length: 6
    max-length: 32

session:
  # Saves player login session per IP address
  enabled: true
  ttl-minutes: 720  # Session lasts for 12 hours before asking for password again

rate-limit:
  # Brute-force protection
  enabled: true
  max-failed-attempts: 5          # Kick on wrong pass. Ban IP after 5 fails
  lockout-duration-minutes: 60    # Ban IP for 1 hour

auth-timeout:
  # Login timer
  seconds: 60
  reminder-interval-seconds: 10   # How often to show the login title/actionbar
```

---

## Tweak Advice

### Low-RAM Hosting
If your server is hosting on a cheap 1GB or 2GB RAM plan, Argon2id might consume too much memory if multiple players join at the same time. You should reduce `memory-kb` to `32768` (32MB) or `16384` (16MB) to avoid server crashes.

### Databases
- SQLite is the easiest setup: it generates a single file locally and requires no configuration.
- Use MySQL if you want to share player registration accounts across multiple servers.
