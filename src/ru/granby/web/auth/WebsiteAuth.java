package ru.granby.web.auth;

import ru.granby.model.entity.User;

public abstract class WebsiteAuth {
    protected long userChatId;
    protected User user;

    public WebsiteAuth(User user, long userChatId) {
        this.user = user;
        this.userChatId = userChatId;
    }

    public boolean tryAuthExistingAccount() {
        return checkToken() || checkLoginPassword();
    }

    public abstract void auth(String login, String password);
    protected abstract boolean checkToken();
    protected abstract boolean checkLoginPassword();
}
