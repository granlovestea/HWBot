package ru.granby.bot.handler.website;

import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.granby.bot.BotContract;
import ru.granby.bot.handler.base.UpdateHandler;
import ru.granby.bot.handler.website.skysmart.SkysmartHandler;

public class ChooseWebsiteHandler extends UpdateHandler {
    private Logger log = LoggerFactory.getLogger(ChooseWebsiteHandler.class);

    public ChooseWebsiteHandler(BotContract.View view, long userChatId) {
        super(view, userChatId);
    }

    @Override
    public boolean tryHandle(Update update) {
        String text = update.message().text();

        switch(text) {
            case SkysmartHandler.name:
                view.startSkysmartHandler(userChatId);
                return true;
            default:
                view.showUnknownWebsite(userChatId);
                return true;
        }
    }
}
