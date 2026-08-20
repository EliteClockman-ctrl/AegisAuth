# AegisAuth Wiki - Commands and Permissions

This reference guide details all command usages, aliases, syntax, and permission nodes for AegisAuth.

---

## Player Commands

These commands are available to standard players by default.

### `/register`
- **Syntax**: `/register <password> <confirmPassword>`
- **Alias**: `/reg`
- **Permission**: `auth.player.register` (Default: true)
- **Description**: Registers a new password credentials block for the player.
- **Constraints**: Can only be run in-game by unauthenticated users.

### `/login`
- **Syntax**: `/login <password>`
- **Aliases**: `/l`, `/log`
- **Permission**: `auth.player.login` (Default: true)
- **Description**: Verifies password credentials and unlocks the player session.

### `/changepassword`
- **Syntax**: `/changepassword <oldPassword> <newPassword>`
- **Alias**: `/changepass`
- **Permission**: `auth.player.changepassword` (Default: true)
- **Description**: Updates the player's password.
- **Constraints**: The player must be logged in. The new password cannot match the old password.

### `/premium`
- **Syntax**: `/premium`
- **Permission**: `auth.player.premium` (Default: true)
- **Description**: Requests activation of official Mojang Premium auto-login mode.

### `/premiumconfirm`
- **Syntax**: `/premiumconfirm`
- **Permission**: `auth.player.premium` (Default: true)
- **Description**: Finalizes Premium activation. Must be run within 60 seconds of `/premium`.

---

## Admin Commands

These commands are restricted to server operators and administrators.

> [!IMPORTANT]
> Administrative commands should only be given to trusted staff.

### `/language`
- **Syntax**: `/language <vietnamese|english>`
- **Alias**: `/lang`
- **Permission**: `auth.admin.language` (Default: OP)
- **Description**: Switches the entire plugin display language (chat messages, actionbars, titles) globally.

### `/unregister`
- **Syntax**: `/unregister <player>`
- **Permission**: `auth.admin.unregister` (Default: OP)
- **Description**: Deletes a player account from the database.
- **Constraints**: 
  > [!WARNING]
  > The target player must be offline for this command to execute successfully. To unregister an online player, use `/authadmin unregister <player>` instead.

### `/unpremium`
- **Syntax**: `/unpremium <player>`
- **Permission**: `auth.admin.unpremium` (Default: OP)
- **Description**: Force-disables Premium auto-login mode for the target player.

### `/authadmin`
- **Syntax**: `/authadmin <reload|unregister> [player]`
- **Permission**: `auth.admin.manage` (Default: OP)
- **Subcommands**:
  - `reload`: Reloads all configurations from `config.yml`.
  - `unregister <player>`: Deletes the target account and kicks the player instantly if they are currently online.
