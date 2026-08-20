# AegisAuth Wiki - Features

AegisAuth is built with a focus on enterprise-grade security and optimized performance. Below is a detailed breakdown of its core features.

---

## 1. Argon2id Password Hashing

Security is our top priority. AegisAuth does not use legacy, weak MD5 or SHA-256 algorithms. Instead, it utilizes Argon2id, which is the winner of the Password Hashing Competition (PHC) and is recommended by OWASP.
- Configurable memory cost (default: 64MB).
- Configurable iterations (default: 3).
- Configurable parallelism (default: 1 thread).
This ensures your player credentials remain secure even if the database is compromised.

---

## 2. Robust Brute-Force and Rate Limit Protection

To protect accounts against automated password guessing, AegisAuth features a built-in rate-limiting firewall:
- Immediate Kick: If a player inputs a wrong password during login, they are disconnected from the server instantly. No login attempt messages are sent to the client to prevent in-game spam.
- IP Lockout: After 5 failed password attempts, the player's IP address is locked for 60 minutes (1 hour).
- Pre-Login Rejection: Locked IP addresses are rejected at the Async Player Pre-Login stage, preventing them from connecting to the server and conserving CPU resources.

---

## 3. Mojang Premium Auto-Login

For servers running in offline mode, AegisAuth offers a secure Mojang Premium integration:
- Initial Registration Required: All players must register their account first.
- Verification: Players can run /premium, which queries the official Mojang API to verify if the account is authentic.
- Confirmation: A /premiumconfirm command must be executed within 60 seconds to prevent accidental lockouts.
- Password Bypass: Once activated, Premium players bypass password prompts and are logged in automatically.
- Deactivation: Admins can use /unpremium <player> to disable Premium status.

---

## 4. Multi-Language System

Administrators can switch the global server language dynamically:
- Switch globally via /language <vietnamese|english> (or /lang <vi|en>).
- Supports 1-to-1 language structure in config.yml.
- Translates all plugin prompts, actionbar countdowns, and titles instantly.

---

## 5. Optimized Performance

- Fully Asynchronous: All database queries (SQLite/MySQL) and cryptographic operations run asynchronously to ensure the main server thread never freezes.
- Adventure API: Built natively using the Adventure MiniMessage component system for modern RGB gradient styling.
- Display Cleanup: Clears Actionbars and Titles instantly upon successful authentication to keep the player screen clean.
