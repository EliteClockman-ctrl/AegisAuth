# AegisAuth Wiki - Commands and Permissions

Below is the complete reference guide for all AegisAuth commands and their associated permissions.

---

## Player Commands

### /register <password> <confirmPassword>
- Description: Registers a new account. Both passwords must match.
- Alias: /reg
- Permission: auth.player.register (Default: true)
- Usage: In-game players only.

### /login <password>
- Description: Authenticates the player session.
- Aliases: /l, /log
- Permission: auth.player.login (Default: true)
- Usage: In-game players only.

### /changepassword <oldPassword> <newPassword>
- Description: Changes the account password.
- Alias: /changepass
- Permission: auth.player.changepassword (Default: true)
- Usage: In-game players only (must be logged in).

### /premium
- Description: Initiates Mojang Premium auto-login activation.
- Alias: None
- Permission: auth.player.premium (Default: true)
- Usage: In-game players only.

### /premiumconfirm
- Description: Confirms Premium auto-login activation. Must be run within 60 seconds of /premium.
- Alias: None
- Permission: auth.player.premium (Default: true)
- Usage: In-game players only.

---

## Admin Commands

### /language <vietnamese|english>
- Description: Changes the global system language of the server.
- Aliases: /lang
- Permission: auth.admin.language (Default: OP)

### /unregister <player>
- Description: Deletes a player account from the database.
- Alias: None
- Permission: auth.admin.unregister (Default: OP)
- Note: The target player must be offline to execute this command.

### /unpremium <player>
- Description: Disables Premium auto-login mode for a target account.
- Alias: None
- Permission: auth.admin.unpremium (Default: OP)

### /authadmin <reload|unregister> [player]
- Description: Reloads the plugin configuration or forces unregistration.
- Alias: None
- Permission: auth.admin.manage (Default: OP)
- Subcommands:
  - reload: Reloads all configuration settings.
  - unregister <player>: Removes the player's account from the database. If they are online, they are kicked instantly.
