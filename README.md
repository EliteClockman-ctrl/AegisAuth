# Description

AegisAuth is a high-performance, enterprise-grade authentication plugin designed specifically for Minecraft Paper, Purpur, and Spigot 1.21.x / 26.1.x servers.

---

## Key Features

- Enterprise Security: Powered by Argon2id password hashing with configurable memory cost, iterations, and parallelism.
- Dual Database Engine: Supports SQLite (file-based default) and MySQL with HikariCP connection pooling for maximum throughput.
- Authentication Flow:
  - Mandatory account registration before accessing game actions.
  - Blindness and Slowness visual/movement impairment while unauthenticated.
  - Full bypass protection: Blocking eating, item swapping, portal teleportation, inventory interaction, entity targeting, and flight until authenticated.
  - IP-based session persistence (Session TTL) for seamless reconnection.
  - Real-time countdown timer displayed via Title and Actionbar.
  - Immediate disconnection (Kick) upon entering an incorrect password.
  - Automatic IP Lockout (1-Hour Ban) after 5 consecutive failed login attempts.
- Mojang Premium Auto-Login Mode:
  - Requires initial account registration before enabling Premium status.
  - /premium command verifies username authenticity against official Mojang/Microsoft servers.
  - Interactive confirmation via /premiumconfirm within 60 seconds.
  - Registered Premium users bypass password prompts and auto-login automatically.
- Admin Management Commands:
  - /unregister <player>: Removes player account from the database (Target player MUST be offline).
  - /unpremium <player>: Disables Premium mode for a target account.
  - /authadmin reload: Reloads all plugin configurations seamlessly.
- Global Multi-Language System (/language):
  - Admin-exclusive command /language <vietnamese|english> (or /lang <vi|en>).
  - Toggles global server display language dynamically across all messages, titles, and actionbars.
  - 1-to-1 synchronized translation mapping in config.yml.
- Visual Excellence & Optimization:
  - Built with MiniMessage and Adventure API supporting hex colors and gradients.
  - Automatic display cleanup (clearDisplay) ensuring no actionbar or title remnants linger on screen after login.

---

## Commands and Permissions

| Command | Aliases | Permission | Default | Description |
| --- | --- | --- | --- | --- |
| /register <password> <confirmPassword> | /reg | auth.player.register | True | Registers a new player account |
| /login <password> | /l, /log | auth.player.login | True | Authenticates an existing account |
| /changepassword <oldPassword> <newPassword> | /changepass | auth.player.changepassword | True | Changes account password |
| /premium | None | auth.player.premium | True | Initiates Premium auto-login activation |
| /premiumconfirm | None | auth.player.premium | True | Confirms Premium auto-login activation |
| /language <vietnamese\|english> | /lang | auth.admin.language | OP | Switches global system language across the server |
| /unregister <player> | None | auth.admin.unregister | OP | Deletes account from database (Target must be offline) |
| /unpremium <player> | None | auth.admin.unpremium | OP | Disables Premium mode for a player |
| /authadmin <reload\|unregister> [player] | None | auth.admin.manage | OP | Administrative control for reloading or unregistering |

---

## Default Configuration (config.yml)

```yaml
language: "vi"

database:
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
  argon2:
    iterations: 3
    memory-kb: 65536
    parallelism: 1
  password:
    min-length: 6
    max-length: 32

session:
  enabled: true
  ttl-minutes: 720

rate-limit:
  enabled: true
  max-failed-attempts: 5
  lockout-duration-minutes: 60

auth-timeout:
  seconds: 60
  reminder-interval-seconds: 10
```

---

## Building and Installation

### System Requirements
- Java Development Kit (JDK) 21 or higher.
- Paper / Purpur / Spigot 1.21.x / 26.1.x server software.

### Compilation Steps
1. Open a terminal or command prompt in the project root directory.
2. Run the Gradle build command:
   - Windows: `.\gradlew build`
   - Linux / macOS: `./gradlew build`
3. The compiled JAR file will be generated at `build/libs/AegisAuth-1.0.0.jar`.
4. Place the JAR file into your server's `plugins/` directory and restart the server.