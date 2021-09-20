package ru.granby.bot.handler;

import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.granby.bot.BotContract;
import ru.granby.bot.handler.base.UpdateHandler;

public class CommonHandler extends UpdateHandler {
    private Logger log = LoggerFactory.getLogger(CommonHandler.class);

    public CommonHandler(BotContract.View view) {
        super(view);
    }

    @Override
    public boolean tryHandle(Update update) {
        log.info("Received new update: " + update.toString());

        try {
            long chatId = update.message().chat().id();
            String updateText = update.message().text();

            switch (updateText) {
                case "/start":
                    view.showAboutBot(chatId);
                    view.showChooseWebsiteMenu(chatId);
                    return true;
            }
        } catch(Exception exception) {
            log.error("Can't handle update: ", exception);
        }

        return false;
    }
}
