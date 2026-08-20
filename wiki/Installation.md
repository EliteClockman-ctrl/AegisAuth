# Installation Guide

Quick guide on how to install and setup AegisAuth.

---

## 1. Requirements

- Paper, Purpur, or Spigot (version 1.21 or higher).
- Java JDK 21 or higher.

---

## 2. Setting Up

1. Download the `AegisAuth-1.0.0.jar` plugin file.
2. Put the JAR file into your server's `/plugins` folder.
3. Start the server (or restart it). The plugin will generate a `/plugins/AegisAuth` folder with `config.yml`.
4. Configure your database settings in `config.yml` (SQLite or MySQL).
5. Reload configuration via command line: `/authadmin reload`.

---

## 3. Database Guide

### SQLite Setup (Recommended)
This is the default configuration. Just leave `type: "SQLITE"` in config.yml. AegisAuth will automatically create a database file called `auth_database.db` inside your plugin directory.

### MySQL Setup
If you need centralized storage:
1. Make sure you have a database created on your MySQL server.
2. Edit database settings in `config.yml`:
   - Change type to `MYSQL`.
   - Put in your host, port, database name, user, and password.
3. Restart your server. AegisAuth will initialize connection pools using HikariCP.
