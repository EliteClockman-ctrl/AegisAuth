package com.authsystem.plugin.config;

public enum MessageKey {
    PREFIX("messages.prefix", "<gradient:#00d2ff:#3a7bd5><b>[AegisAuth]</b></gradient> ", "<gradient:#00d2ff:#3a7bd5><b>[AegisAuth]</b></gradient> "),
    
    REGISTER_PROMPT("messages.register-prompt", "<yellow>Vui lòng đăng ký tài khoản:</yellow> <white>Gõ <yellow>/register <mật khẩu> <xác nhận></yellow> để bắt đầu chơi.</white>", "<yellow>Please register your account:</yellow> <white>Type <yellow>/register <password> <confirm></yellow> to start playing.</white>"),
    REGISTER_SUCCESS("messages.register-success", "<green>Đăng ký tài khoản thành công! Chúc bạn chơi game vui vẻ.</green>", "<green>Account registered successfully! Have fun playing.</green>"),
    REGISTER_ALREADY("messages.register-already", "<red>Tài khoản của bạn đã được đăng ký! Dùng /login <mật khẩu>.</red>", "<red>Your account is already registered! Use /login <password>.</red>"),
    REGISTER_PASSWORD_MISMATCH("messages.register-password-mismatch", "<red>Mật khẩu xác nhận không trùng khớp! Vui lòng thử lại.</red>", "<red>Confirmation password does not match! Please try again.</red>"),
    REGISTER_PASSWORD_TOO_SHORT("messages.register-password-too-short", "<red>Mật khẩu quá ngắn! Tối thiểu phải có <yellow>{min}</yellow> ký tự.</red>", "<red>Password too short! Minimum required is <yellow>{min}</yellow> characters.</red>"),
    REGISTER_PASSWORD_TOO_LONG("messages.register-password-too-long", "<red>Mật khẩu quá dài! Tối đa chỉ được <yellow>{max}</yellow> ký tự.</red>", "<red>Password too long! Maximum allowed is <yellow>{max}</yellow> characters.</red>"),
    REGISTER_PASSWORD_BLACKLISTED("messages.register-password-blacklisted", "<red>Mật khẩu quá đơn giản và không an toàn! Vui lòng chọn mật khẩu khác.</red>", "<red>Password is too simple and unsafe! Please choose another password.</red>"),
    REGISTER_USAGE("messages.register-usage", "<red>Cú pháp: /register <mật khẩu> <xác nhận mật khẩu></red>", "<red>Usage: /register <password> <confirmPassword></red>"),

    LOGIN_PROMPT("messages.login-prompt", "<aqua>Vui lòng đăng nhập:</aqua> <white>Gõ <aqua>/login <mật khẩu></aqua> để mở khóa nhân vật.</white>", "<aqua>Please log in:</aqua> <white>Type <aqua>/login <password></aqua> to unlock your character.</white>"),
    LOGIN_SUCCESS("messages.login-success", "<green>Đăng nhập thành công! Chào mừng bạn quay trở lại server.</green>", "<green>Logged in successfully! Welcome back to the server.</green>"),
    LOGIN_ALREADY("messages.login-already", "<yellow>Bạn đã đăng nhập vào hệ thống rồi!</yellow>", "<yellow>You are already logged in to the system!</yellow>"),
    LOGIN_NOT_REGISTERED("messages.login-not-registered", "<red>Tài khoản này chưa đăng ký! Dùng /register <mật khẩu> <xác nhận>.</red>", "<red>This account is not registered! Use /register <password> <confirm>.</red>"),
    LOGIN_WRONG_PASSWORD("messages.login-wrong-password", "<red>Mật khẩu không chính xác! Bạn đã bị ngắt kết nối.</red>", "<red>Incorrect password! You have been disconnected.</red>"),
    LOGIN_SESSION_RESTORED("messages.login-session-restored", "<green>Đã tự động đăng nhập thông qua Session IP an toàn.</green>", "<green>Auto-logged in via secure IP Session.</green>"),
    LOGIN_USAGE("messages.login-usage", "<red>Cú pháp: /login <mật khẩu></red>", "<red>Usage: /login <password></red>"),

    CHANGEPASS_SUCCESS("messages.changepass-success", "<green>Đổi mật khẩu thành công!</green>", "<green>Password changed successfully!</green>"),
    CHANGEPASS_WRONG_OLD("messages.changepass-wrong-old", "<red>Mật khẩu cũ không chính xác!</red>", "<red>Old password is incorrect!</red>"),
    CHANGEPASS_SAME_AS_OLD("messages.changepass-same-as-old", "<red>Mật khẩu mới không được trùng với mật khẩu cũ!</red>", "<red>New password cannot be the same as old password!</red>"),
    CHANGEPASS_NOT_LOGGED_IN("messages.changepass-not-logged-in", "<red>Bạn phải đăng nhập trước khi đổi mật khẩu!</red>", "<red>You must log in before changing password!</red>"),
    CHANGEPASS_USAGE("messages.changepass-usage", "<red>Cú pháp: /changepassword <mật khẩu cũ> <mật khẩu mới></red>", "<red>Usage: /changepassword <oldPassword> <newPassword></red>"),

    PREMIUM_PROMPT_CONFIRM("messages.premium-prompt-confirm", "<yellow>[CẢNH BÁO PREMIUM]</yellow>\n<yellow>Bạn đang kích hoạt chế độ Tự Động Đăng Nhập Bản Quyền (Premium).</yellow>\n<white>LƯU Ý: Bạn chỉ có thể đăng nhập bằng tài khoản Mojang/Microsoft chính chủ có tên này.</white>\n<white>Gõ <gold>/premiumconfirm</gold> trong vòng <aqua>60 giây</aqua> để xác nhận.</white>", "<yellow>[PREMIUM WARNING]</yellow>\n<yellow>You are activating Premium Auto-Login mode.</yellow>\n<white>NOTE: You can only log in with an official Mojang/Microsoft account under this username.</white>\n<white>Type <gold>/premiumconfirm</gold> within <aqua>60 seconds</aqua> to confirm.</white>"),
    PREMIUM_ALREADY("messages.premium-already", "<yellow>Tài khoản của bạn đã ở chế độ Premium!</yellow>", "<yellow>Your account is already in Premium mode!</yellow>"),
    PREMIUM_NOT_REGISTERED("messages.premium-not-registered", "<red>Tài khoản của bạn chưa được đăng ký! Vui lòng dùng /register trước khi kích hoạt Premium.</red>", "<red>Your account is not registered! Please use /register before activating Premium.</red>"),
    PREMIUM_NOT_MOJANG("messages.premium-not-mojang", "<red>Tên nhân vật của bạn không tồn tại trên hệ thống Mojang chính chủ!</red>", "<red>Your username does not exist on official Mojang servers!</red>"),
    PREMIUM_NO_PENDING("messages.premium-no-pending", "<red>Bạn chưa kích hoạt lệnh /premium hoặc phiên xác nhận đã hết hạn (60s)!</red>", "<red>You have not run /premium or confirmation session expired (60s)!</red>"),
    PREMIUM_CONFIRMED("messages.premium-confirmed", "<green>Kích hoạt chế độ Premium thành công! Từ lần sau bạn sẽ được tự động đăng nhập.</green>", "<green>Premium mode activated successfully! You will auto-login next time.</green>"),
    PREMIUM_AUTO_LOGIN("messages.premium-auto-login", "<green>Xác thực tài khoản Mojang Premium thành công! Đã tự động đăng nhập.</green>", "<green>Mojang Premium authentication successful! Auto-logged in.</green>"),

    ACTION_BLOCKED("messages.action-blocked", "<red>Bạn cần phải đăng nhập hoặc đăng ký trước khi thực hiện hành động này!</red>", "<red>You need to log in or register before performing this action!</red>"),
    TIMEOUT_KICK("messages.timeout-kick", "<red>Bạn đã bị ngắt kết nối vì không đăng nhập/đăng ký đúng thời gian!</red>", "<red>You were disconnected for not logging in/registering in time!</red>"),
    DUPLICATE_ONLINE_KICK("messages.duplicate-online-kick", "<red>Tài khoản này đã có người đang đăng nhập trên máy chủ!</red>", "<red>An account with this name is already logged in on the server!</red>"),
    RATE_LIMITED_KICK("messages.rate-limited-kick", "<red>Địa chỉ IP của bạn bị tạm khóa trong {minutes} phút do nhập sai mật khẩu quá 5 lần!</red>", "<red>Your IP address is temporarily locked for {minutes} minutes due to entering the wrong password 5 times!</red>"),

    ADMIN_UNPREMIUM_SUCCESS("messages.admin-unpremium-success", "<green>Đã hủy chế độ Premium cho <yellow>{player}</yellow> thành công!</green>", "<green>Disabled Premium mode for <yellow>{player}</yellow> successfully!</green>"),
    ADMIN_UNPREMIUM_NOT_PREMIUM("messages.admin-unpremium-not-premium", "<yellow>Tài khoản của <player> hiện không ở chế độ Premium!</yellow>", "<yellow>Account <player> is not currently in Premium mode!</yellow>"),
    ADMIN_UNPREMIUM_USAGE("messages.admin-unpremium-usage", "<red>Cú pháp: /unpremium <mật khẩu|người chơi></red>", "<red>Usage: /unpremium <player></red>"),
    ADMIN_LANGUAGE_CHANGED_VI("messages.admin-language-changed-vi", "<green>✔ Đã chuyển toàn bộ ngôn ngữ giao diện plugin sang <yellow>Tiếng Việt</yellow>!</green>", "<green>✔ Entire plugin system language changed to <yellow>Vietnamese</yellow>!</green>"),
    ADMIN_LANGUAGE_CHANGED_EN("messages.admin-language-changed-en", "<green>✔ Đã chuyển toàn bộ ngôn ngữ giao diện plugin sang <yellow>English</yellow>!</green>", "<green>✔ Entire plugin system language changed to <yellow>English</yellow>!</green>"),
    ADMIN_PLAYER_NOT_FOUND("messages.admin-player-not-found", "<red>Không tìm thấy dữ liệu tài khoản <yellow>{player}</yellow>!</red>", "<red>Account data for <yellow>{player}</yellow> was not found!</red>"),
    ADMIN_RELOAD_SUCCESS("messages.admin-reload-success", "<green>Đã nạp lại toàn bộ cấu hình AegisAuth!</green>", "<green>AegisAuth configuration reloaded successfully!</green>"),
    ADMIN_UNREGISTER_SUCCESS("messages.admin-unregister-success", "<green>Đã xóa tài khoản <yellow>{player}</yellow> khỏi hệ thống!</green>", "<green>Deleted account <yellow>{player}</yellow> from the system!</green>"),
    ADMIN_UNREGISTER_PLAYER_ONLINE("messages.admin-unregister-player-online", "<red>Người chơi <yellow>{player}</yellow> đang online! Người chơi phải offline trước khi thực hiện unregister.</red>", "<red>Player <yellow>{player}</yellow> is online! Player must be offline to unregister.</red>"),
    ADMIN_UNREGISTER_USAGE("messages.admin-unregister-usage", "<red>Cú pháp: /unregister <người chơi></red>", "<red>Usage: /unregister <player></red>"),
    ADMIN_NO_PERMISSION("messages.admin-no-permission", "<red>Bạn không có quyền hạn thực hiện lệnh này!</red>", "<red>You do not have permission to execute this command!</red>"),
    ADMIN_USAGE("messages.admin-usage", "<red>Cú pháp: /authadmin <reload|unregister> [player]</red>", "<red>Usage: /authadmin <reload|unregister> [player]</red>"),
    PLAYER_ONLY("messages.player-only", "<red>Lệnh này chỉ dành cho người chơi trong game!</red>", "<red>This command is for in-game players only!</red>"),

    TITLE_WELCOME_HEADER("messages.title-welcome-header", "<gradient:#00c6ff:#0072ff>CHÀO MỪNG</gradient>", "<gradient:#00c6ff:#0072ff>WELCOME</gradient>"),
    TITLE_WELCOME_FOOTER("messages.title-welcome-footer", "<gray>Vui lòng đăng nhập hoặc đăng ký để tiếp tục</gray>", "<gray>Please log in or register to continue</gray>"),
    ACTIONBAR_LOGIN_COUNTDOWN("messages.actionbar-login-countdown", "<yellow>Thời gian đăng nhập còn lại: <red>{seconds}s</red> | Gõ: <aqua>/login <pass></aqua></yellow>", "<yellow>Login time remaining: <red>{seconds}s</red> | Type: <aqua>/login <pass></aqua></yellow>"),
    ACTIONBAR_REGISTER_COUNTDOWN("messages.actionbar-register-countdown", "<yellow>Thời gian đăng ký còn lại: <red>{seconds}s</red> | Gõ: <gold>/register <pass> <pass></gold></yellow>", "<yellow>Register time remaining: <red>{seconds}s</red> | Type: <gold>/register <pass> <pass></gold></yellow>"),

    LANGUAGE_CHANGED_VI("messages.language-changed-vi", "<green>✔ Đã chuyển ngôn ngữ hiển thị sang <yellow>Tiếng Việt</yellow>!</green>", "<green>✔ Display language changed to <yellow>Vietnamese</yellow>!</green>"),
    LANGUAGE_CHANGED_EN("messages.language-changed-en", "<green>✔ Đã chuyển ngôn ngữ hiển thị sang <yellow>English</yellow>!</green>", "<green>✔ Display language changed to <yellow>English</yellow>!</green>"),
    LANGUAGE_USAGE("messages.language-usage", "<yellow>Cú pháp: /language <vietnamese|english> (Hoặc: /lang <vi|en>)</yellow>", "<yellow>Usage: /language <vietnamese|english> (Or: /lang <vi|en>)</yellow>");

    private final String path;
    private final String defaultViMessage;
    private final String defaultEnMessage;

    MessageKey(String path, String defaultViMessage) {
        this(path, defaultViMessage, defaultViMessage);
    }

    MessageKey(String path, String defaultViMessage, String defaultEnMessage) {
        this.path = path;
        this.defaultViMessage = defaultViMessage;
        this.defaultEnMessage = defaultEnMessage;
    }

    public String getPath() {
        return path;
    }

    public String getDefaultMessage() {
        return defaultViMessage;
    }

    public String getDefaultMessage(String lang) {
        if ("en".equalsIgnoreCase(lang)) {
            return defaultEnMessage;
        }
        return defaultViMessage;
    }
}
