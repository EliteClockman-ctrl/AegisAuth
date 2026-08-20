# AegisAuth Wiki - Installation Guide

Follow this guide to install, configure, and verify AegisAuth on your server.

---

## 1. System Requirements

Before downloading, check if your host meets these specifications:
- **Server Platform**: Paper, Purpur, or Spigot (1.21 or higher).
- **Java Runtime**: JDK 21 or higher.
- **Memory**: At least 100MB of free heap memory for Argon2id hashing.

---

## 2. Installation Steps

Follow these instructions for a clean installation:

1. **Download**: Download the `AegisAuth-1.0.0.jar` binary file.
2. **Upload**: Place the JAR file into the `/plugins` directory of your server.
3. **Start**: Start the server to generate the default configuration files.
4. **Configure**: Open `/plugins/AegisAuth/config.yml` to set your preferences.
5. **Reload**: Run `/authadmin reload` in the console to apply any changes.

---

## 3. Database Selection Guide

AegisAuth supports SQLite and MySQL. Choose the database that fits your setup:

### SQLite (Single Server)
- Best for standalone servers.
- Requires no external software.
- Fast, secure, and auto-generated locally.

### MySQL (Centralized / BungeeCord / Velocity)
- Best for multiple servers sharing player accounts.
- Requires a running MySQL/MariaDB database server.

> [!IMPORTANT]
> When switching from SQLite to MySQL, player accounts will not be migrated automatically. You must manually copy the account rows or start fresh.
