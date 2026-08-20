# AegisAuth Wiki - Developers Guide

Welcome to the AegisAuth developers guide. This page explains building the plugin, source code organization, and architectural overview.

---

## 1. Prerequisites and Build Environment

To compile AegisAuth from source code:
- Ensure Java JDK 21 is installed.
- Ensure Gradle 8.8 wrapper is used.

### Compilation Commands
Run the following build command in the project root:
- Windows: `.\gradlew clean build`
- Linux / macOS: `./gradlew clean build`

The output shadow jar containing shaded dependencies will be generated in:
`build/libs/AegisAuth-1.0.0.jar`

---

## 2. Technical Architecture

AegisAuth is designed with modular packages:

### com.authsystem.plugin
- AuthPlugin.java: Main entry point. Handles dependency injection, listeners registration, and command binding.

### com.authsystem.plugin.config
- ConfigManager.java: Handles loading and reloading config.yml, resolving language files, and translating keys.
- MessageKey.java: Enumerates all message codes with English and Vietnamese fallbacks.

### com.authsystem.plugin.security
- PasswordHasher.java: Implements Argon2id password hashing utilizing Argon2-JVM library.
- RateLimiter.java: Uses Caffeine caches to track wrong password attempts per IP and manage lockouts.
- SessionManager.java: Manages local memory maps for active player IP sessions.

### com.authsystem.plugin.database
- DatabaseManager.java: Establishes HikariCP datasource for MySQL or JDBC connection for SQLite.
- AuthDao.java: Performs async database operations for user registration, credentials retrieval, metadata, and Premium flags.
- AuditDao.java: Logs authentication audit events.

### com.authsystem.plugin.listener
- PlayerPreLoginListener.java: Rejects connection attempts from locked IPs.
- PlayerJoinQuitListener.java: Manages join, auto-login, and session creation.
- PlayerRestrictionListener.java: Blocks movements, chat, inventory, commands, block breaks, and damage events before logging in.
