# Commands and Permissions

Reference guide for all AegisAuth command commands and permissions.

---

## Player Commands

### /register <password> <confirmPassword>
- **Alias**: `/reg`
- **Permission**: `auth.player.register` (Default: true)
- **Description**: Registers a new password. Both fields must match.

### /login <password>
- **Aliases**: `/l`, `/log`
- **Permission**: `auth.player.login` (Default: true)
- **Description**: Authenticates the player session.

### /changepassword <oldPassword> <newPassword>
- **Alias**: `/changepass`
- **Permission**: `auth.player.changepassword` (Default: true)
- **Description**: Changes your password. You must be logged in.

### /premium
- **Permission**: `auth.player.premium` (Default: true)
- **Description**: Kicks off Mojang premium auto-login setup.

### /premiumconfirm
- **Permission**: `auth.player.premium` (Default: true)
- **Description**: Confirms premium auto-login within 60 seconds.

---

## Admin Commands

### /language <vietnamese|english>
- **Alias**: `/lang`
- **Permission**: `auth.admin.language` (Default: OP)
- **Description**: Switches the system-wide language between English and Vietnamese.

### /unregister <player>
- **Permission**: `auth.admin.unregister` (Default: OP)
- **Description**: Deletes a player account from the database.
- **Note**: The target player must be offline. If they are online, use `/authadmin unregister <player>` instead.

### /unpremium <player>
- **Permission**: `auth.admin.unpremium` (Default: OP)
- **Description**: Turns off premium auto-login for a specific player.

### /authadmin <reload|unregister> [player]
- **Permission**: `auth.admin.manage` (Default: OP)
- **Subcommands**:
  - `reload`: Reloads all configs from config.yml.
  - `unregister <player>`: Deletes the account and kicks the player instantly if they are online.
