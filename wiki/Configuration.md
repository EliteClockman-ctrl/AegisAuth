# AegisAuth Wiki - Configuration

AegisAuth is highly customizable. The config.yml file is divided into several main sections: general, database, security, session, rate-limiting, and timing.

---

## Complete Guide to config.yml

### General Settings
- language: Sets the default global display language (e.g., "vi" for Vietnamese, "en" for English). Can be updated in-game using /language.

### Database Settings
- type: Database type, choose either "SQLITE" or "MYSQL".
- sqlite.file: Filename of the SQLite database (e.g., "auth_database.db").
- mysql.host: MySQL host address.
- mysql.port: MySQL port (default: 3306).
- mysql.database: Name of the database.
- mysql.username: MySQL username.
- mysql.password: MySQL password.
- mysql.use-ssl: Sets whether to use SSL connection encryption (default: false).

### Security & Argon2 Settings
- security.argon2.iterations: Computational complexity (number of passes over memory).
- security.argon2.memory-kb: Memory cost in kilobytes (default: 65536 = 64MB).
- security.argon2.parallelism: Number of parallel threads (default: 1).
- security.password.min-length: Minimum password length (default: 6).
- security.password.max-length: Maximum password length (default: 32).

### Session Settings
- session.enabled: Enables auto-login session restore on IP address match (default: true).
- session.ttl-minutes: Duration of session validity in minutes (default: 720 minutes = 12 hours).

### Rate Limiting Settings
- rate-limit.enabled: Enables brute-force protection (default: true).
- rate-limit.max-failed-attempts: Maximum allowed login failures (default: 5).
- rate-limit.lockout-duration-minutes: IP ban lockout time in minutes (default: 60 minutes = 1 hour).

### Auth Timeout Settings
- auth-timeout.seconds: Time allowed for players to log in or register before being kicked (default: 60 seconds).
- auth-timeout.reminder-interval-seconds: Time interval between actionbar and title reminders (default: 10 seconds).
