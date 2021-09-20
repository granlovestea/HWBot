package ru.granby.bot.handler;

import com.pengrad.telegrambot.model.Update;
import ru.granby.bot.BotContract;
import ru.granby.bot.handler.base.UpdateHandler;

public class ErrorHandler extends UpdateHandler {
    public ErrorHandler(BotContract.View view) {
        super(view);
    }

    @Override
    public boolean tryHandle(Update update) {
        long chatId = update.message().chat().id();

        view.showUnknownCommandError(chatId);
        view.showChooseWebsiteMenu(chatId);

        return true;
    }
}
