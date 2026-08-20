# AegisAuth Wiki - Developers Guide

This guide is intended for developers who wish to compile, extend, or contribute to AegisAuth.

---

## 1. Prerequisites and Compilation

To build AegisAuth, ensure you have:
- Java JDK 21 installed.
- Access to the command line.

### Compiling the Shaded JAR
AegisAuth shades its dependencies (Argon2, HikariCP, Caffeine) to prevent conflict with other plugins.

Run the build command:
```bash
# Windows
.\gradlew clean build

# Linux / macOS
./gradlew clean build
```

After a successful compilation, only the shaded fat-jar is outputted:
`build/libs/AegisAuth-1.0.0.jar`

---

## 2. Technical Architecture

AegisAuth divides its logic into distinct modular packages:

### Database & Asynchronous Operations
All database queries are executed off-thread using Java's `CompletableFuture` API.
- **HikariCP**: Manages connection pooling for MySQL.
- **DAO Pattern**: Separate `AuthDao` (auth account storage) and `AuditDao` (admin auditing logs).

### Relocated Dependencies
To avoid classpath conflicts, the following libraries are shaded and relocated:
- `de.mkammerer.argon2` -> `com.authsystem.plugin.libs.argon2`
- `com.zaxxer.hikari` -> `com.authsystem.plugin.libs.hikari`
- `com.github.benmanes.caffeine` -> `com.authsystem.plugin.libs.caffeine`

---

## 3. Contributing

We accept pull requests on GitHub. Please ensure that:
1. Any database changes include migrations or fail-safes.
2. All unit tests (`.\gradlew test`) pass successfully before submitting code.
3. No external dependencies are added without relocation shadowing.
