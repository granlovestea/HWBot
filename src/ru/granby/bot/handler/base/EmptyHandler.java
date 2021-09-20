package ru.granby.bot.handler.base;

import com.pengrad.telegrambot.model.Update;
import ru.granby.bot.BotContract;

public class EmptyHandler extends UpdateHandler {
    public EmptyHandler(BotContract.View view) {
        super(view);
    }

    @Override
    public boolean tryHandle(Update update) {
        return false;
    }
}
