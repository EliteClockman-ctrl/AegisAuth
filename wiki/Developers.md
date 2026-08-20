# Developers Guide

Quick guide on how to build and understand the AegisAuth codebase.

---

## 1. How to Build

We use Gradle as the build tool. You need JDK 21 installed.

To build the plugin jar:
```bash
# Windows
.\gradlew clean build

# Linux / macOS
./gradlew clean build
```

This outputs a shaded jar at `build/libs/AegisAuth-1.0.0.jar`.

> [!NOTE]
> AegisAuth disables the default `jar` task and only outputs the `shadowJar` version. This shaded jar relocates external dependencies (HikariCP, Caffeine, Argon2) to prevent version conflicts with other plugins on your server.

---

## 2. Package Structure

- **com.authsystem.plugin**: Entry point class (`AuthPlugin.java`). Sets up listeners, command executors, and database tables.
- **com.authsystem.plugin.security**: Core logic for Argon2id hashing, rate limiting attempts cache (Caffeine), and session IPs.
- **com.authsystem.plugin.database**: Handles SQL operations. All tasks are run asynchronously via `CompletableFuture` API to prevent main thread blocking.
- **com.authsystem.plugin.listener**: Rejects connections from locked IPs (`PlayerPreLoginListener`), controls restrictions for unlogged players (`PlayerRestrictionListener`), and handles auto-logins (`PlayerJoinQuitListener`).
