# AegisAuth Wiki - Installation

Setting up AegisAuth on your server is quick and simple.

---

## 1. System Requirements

Ensure your hosting environment meets the following conditions:
- Server Software: Paper, Purpur, or Spigot (version 1.21 or higher).
- Java Environment: JDK 21 or higher (older Java runtimes will fail to load the class files).

---

## 2. Basic Installation Step-by-Step

1. Obtain the compiled AegisAuth-1.0.0.jar (either compile it yourself or download the release).
2. Stop your Minecraft server.
3. Place the AegisAuth-1.0.0.jar file into the server plugins directory:
   /plugins
4. Start the server. This will generate the config.yml file inside the /plugins/AegisAuth directory.

---

## 3. Database Configuration

AegisAuth supports SQLite (default) and MySQL.

### Setting up SQLite
By default, the type is set to SQLITE:
```yaml
database:
  type: "SQLITE"
  sqlite:
    file: "auth_database.db"
```
No external software is required. The database file will be created automatically in your plugins/AegisAuth directory.

### Setting up MySQL
If you are running a network of servers or want centralized storage:
1. Open plugins/AegisAuth/config.yml.
2. Edit database.type to MYSQL.
3. Fill in your database connection credentials:
```yaml
database:
  type: "MYSQL"
  mysql:
    host: "localhost"
    port: 3306
    database: "minecraft_auth"
    username: "your_username"
    password: "your_password"
    use-ssl: false
```
4. Restart the server. AegisAuth will use HikariCP connection pool to establish asynchronous database connections.
