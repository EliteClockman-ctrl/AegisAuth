# AegisAuth Wiki - Security Features

This page provides an in-depth explanation of the security mechanisms, cryptography, and rate-limiting features built into AegisAuth.

---

## Table of Contents
1. [Argon2id Hashing Scheme](#1-argon2id-hashing-scheme)
2. [Brute-Force Lockout Firewall](#2-brute-force-lockout-firewall)
3. [Mojang Premium Auto-Login](#3-mojang-premium-auto-login)
4. [Asynchronous Thread Management](#4-asynchronous-thread-management)

---

## 1. Argon2id Hashing Scheme

AegisAuth implements the Argon2id hashing algorithm, the winner of the Password Hashing Competition (PHC) and the official recommendation of OWASP.

### Hashing Algorithm Comparison

| Feature | MD5 | SHA-256 | Argon2id (AegisAuth) |
| --- | --- | --- | --- |
| **GPU Crack Resistance** | None (Extremely Weak) | Poor | Excellent (Memory-Hard) |
| **ASIC Crack Resistance** | None (Extremely Weak) | Poor | Excellent (Memory-Hard) |
| **Salt Type** | None or Static | Per-user | Custom generated per password |
| **Configurability** | None | None | Iterations, Memory Cost, Parallelism |

### Cryptographic Configuration Parameters
- **Memory Cost**: Memory size allocated during computation (default: 64MB). This blocks GPU-based dictionary attacks because GPUs have limited memory cache per thread.
- **Time Cost (Iterations)**: Number of computation passes (default: 3).
- **Parallelism**: Number of parallel CPU threads utilized during execution (default: 1).

---

## 2. Brute-Force Lockout Firewall

To defend against online dictionary attacks, AegisAuth contains a built-in rate-limiting firewall.

> [!WARNING]
> By default, AegisAuth uses a strict immediate kick policy to prevent in-game bot spam and screen freezing.

### How the Lockout Works
1. **Wrong Password Entry**: If a player enters an incorrect password during `/login`, they are immediately disconnected from the server with the `LOGIN_WRONG_PASSWORD` kick message.
2. **Attempt Tracker**: Failed attempts per IP are cached using Caffeine.
3. **Lockout Trigger**: Upon reaching the 5th failed attempt, the IP address is banned from connecting to the server for 60 minutes (1 hour).
4. **Pre-Login Defense**: Reconnection attempts from a banned IP are blocked at the `AsyncPlayerPreLoginEvent` stage, saving valuable CPU cycles.

---

## 3. Mojang Premium Auto-Login

> [!IMPORTANT]
> To prevent malicious players from stealing premium usernames, AegisAuth requires players to register their accounts manually before enabling Premium status.

### Premium Lifecycle Flow
1. **Initial Registration**: The user registers via `/register <password> <confirm>`.
2. **Premium Request**: The user runs `/premium`. The plugin queries Mojang's API to ensure the account username exists.
3. **Interactive Confirmation**: The player must confirm by typing `/premiumconfirm` within 60 seconds.
4. **Auto-Login**: On subsequent logins, the server recognizes the Mojang authentication session and logs the user in automatically, bypassing password prompts.
5. **Recovery**: Administrators can run `/unpremium <player>` to disable the auto-login flag if a user changes their account type.
