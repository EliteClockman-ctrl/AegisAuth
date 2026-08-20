# AegisAuth - Welcome to the Wiki

AegisAuth is a high-performance, enterprise-grade authentication plugin designed specifically for modern Minecraft Paper and Purpur 1.21+ servers. 

---

## Documentation Quick Links

To get started with AegisAuth, select one of the following topics:

### Getting Started
- [Installation Guide](Installation): System requirements, plugin setup, and database initialization.
- [Configuration Reference](Configuration): Line-by-line configuration parameters for config.yml.

### Plugin Features
- [Security Features](Features): Argon2id password hashing, brute-force IP lockout, and Mojang premium auto-login.
- [Commands and Permissions Reference](Commands-and-Permissions): Commands for both players and administrators.

### Developers
- [Developers Guide](Developers): Compilation instructions, build tasks, and package architecture.

---

## Why Choose AegisAuth?

> [!NOTE]
> Most Minecraft authentication plugins still rely on obsolete algorithms like MD5 or SHA-256, which can be cracked in seconds using modern GPUs. AegisAuth brings industry-standard cryptographic practices to Minecraft.

### Key Highlights
- **Argon2id Cryptography**: High resistance against memory-hard GPU and ASIC attack vectors.
- **Asynchronous Execution**: Every database transaction and cryptographic hash is computed off-thread to prevent server lag.
- **Dynamic Lockout Firewall**: Auto-bans IPs at the pre-login stage after repeated failed login attempts.
- **Premium Bypass**: Seamless automatic login for official Mojang/Microsoft account owners.
- **Dynamic Language Swapper**: Swap the entire system language dynamically between English and Vietnamese.
