# AegisAuth Wiki

AegisAuth is a simple login and registration plugin for Minecraft Paper and Purpur 1.21+ servers. 

Unlike older authentication plugins that use weak hashing algorithms like MD5 or SHA-256, AegisAuth uses Argon2id to keep player passwords secure. Every database query and password check is run asynchronously, so it will not lag your main server thread.

---

## Navigation

- [Features](Features): Why we use Argon2id, how the rate-limiter works, and premium auto-login.
- [Installation Guide](Installation): Setting up the plugin with SQLite or MySQL databases.
- [Configuration](Configuration): Tuning config.yml settings.
- [Commands and Permissions](Commands-and-Permissions): Commands for players and admins.
- [Developers Guide](Developers): Building from source and codebase overview.

---

## Quick Overview

- **Argon2id hashing**: Modern password encryption designed to stop GPU cracking.
- **Async by default**: Database queries (SQLite/MySQL) run off-thread to prevent server TPS drops.
- **IP Lockout**: Automatic 1-hour ban after 5 wrong password attempts. The check runs at pre-login to save server resources.
- **Mojang Auto-Login**: Premium players can bypass password prompts safely.
- **Bilingual**: Switch between English and Vietnamese dynamically.
