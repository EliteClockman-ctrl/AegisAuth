# Plugin Features

Here is how the core features of AegisAuth work under the hood.

---

## 1. Why Argon2id?

Most legacy auth plugins use MD5 or SHA-256. With modern hardware, hackers can calculate billions of these hashes per second. If someone steals your database, they can easily crack those passwords using standard graphics cards.

Argon2id is a memory-hard hashing algorithm. It forces the system to use RAM and CPU time to calculate a single hash, making it extremely difficult and expensive for hackers to brute-force your passwords.

### Configuration Settings
You can tweak these settings in config.yml:
- **memory-kb**: The RAM allocated for hashing one password (default: 64MB). Do not set this too high if your server runs on a low-RAM host.
- **iterations**: How many times the algorithm runs (default: 3). Higher values mean better security but take more CPU time.
- **parallelism**: How many CPU threads to use (default: 1).

---

## 2. Rate Limiting and Brute-Force Prevention

If someone is trying to guess a password:
1. **Immediate Kick**: They get kicked instantly on their first wrong password attempt. No messages are sent to in-game chat to prevent bot spam.
2. **Attempt Tracking**: We track failed attempts per IP address using Caffeine cache.
3. **Lockout**: On the 5th failed attempt, the IP is banned for 60 minutes.
4. **Pre-Login Check**: Banned IPs are blocked at the PlayerPreLogin stage. The server rejects them before they even join, saving CPU and bandwidth.

---

## 3. Premium Auto-Login

This feature allows official Mojang account owners to join without entering passwords.

### Setup Flow
1. The player joins and registers their account normally.
2. The player types `/premium`. The plugin checks Mojang APIs to make sure the username is registered.
3. The player must confirm by typing `/premiumconfirm` within 60 seconds (this prevents accidental lockouts if they made a mistake).
4. From the next login, they will bypass password screens entirely.

> [!NOTE]
> If a player changes their username or account status, admins can disable this using `/unpremium <player>`.
