package ru.granby.bot.handler.base;

import com.pengrad.telegrambot.model.Update;
import ru.granby.Application;
import ru.granby.bot.BotContract;
import ru.granby.model.entity.User;

public abstract class UpdateHandler {
    protected BotContract.View view;
    protected User user;
    protected long userChatId;

    public UpdateHandler(BotContract.View view, long userChatId) {
        this.view = view;
        this.userChatId = userChatId;
        this.user = Application.getUserStorage().getUserByChatId(userChatId);
    }

    public UpdateHandler(BotContract.View view) {
        this.view = view;
    }

    public abstract boolean tryHandle(Update update);
}
