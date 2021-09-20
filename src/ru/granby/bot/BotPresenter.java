package ru.granby.bot;


import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.granby.Application;
import ru.granby.bot.handler.CommonHandler;
import ru.granby.bot.handler.ErrorHandler;
import ru.granby.bot.handler.base.EmptyHandler;
import ru.granby.bot.handler.base.UpdateHandler;
import ru.granby.bot.handler.website.ChooseWebsiteHandler;
import ru.granby.bot.handler.website.skysmart.SkysmartHandler;
import ru.granby.model.entity.User;

public class BotPresenter implements BotContract.Presenter {
    private Logger log = LoggerFactory.getLogger(BotPresenter.class);
    private BotContract.View view;
    private UpdateHandler commonHandler;
    private UpdateHandler errorHandler;

    public BotPresenter(BotContract.View view) {
        this.view = view;
        this.commonHandler = new CommonHandler(view);
        this.errorHandler = new ErrorHandler(view);
    }

    @Override
    public void onUpdate(Update update) {
        try {
            long chatId = update.message().chat().id();
            Application.getUserStorage().tryAddUser(chatId, new User(
                    update.message().chat().username(),
                    getStartHandler()
            ));
            UpdateHandler userHandler = Application.getUserStorage().getUserByChatId(chatId).getHandler();

            handleWithChain(update, new UpdateHandler[]{
                    commonHandler,
                    userHandler,
                    errorHandler
            });
        } catch (Exception exception) {
            System.out.println("BotPresenter onUpdate: " + exception);
        }
    }

    @Override
    public void setChooseWebsiteHandler(long chatId) {
        setUserHandler(chatId, new ChooseWebsiteHandler(view, chatId));
    }

    @Override
    public void startSkysmartHandler(long chatId) {
        SkysmartHandler skysmartHandler = new SkysmartHandler(view, chatId);
        setUserHandler(chatId, skysmartHandler);
        skysmartHandler.start();
    }

    @Override
    public void setEmptyHandler(long chatId) {
        setUserHandler(chatId, new EmptyHandler(view));
    }

    private UpdateHandler getStartHandler() {
        return new EmptyHandler(view);
    }

    private boolean handleWithChain(Update update, UpdateHandler[] handlersChain) {
        for (UpdateHandler handler : handlersChain) {
            try {
                if (handler.tryHandle(update)) {
                    return true;
                }
            } catch (Exception exception) {
                log.error(String.format("Can't handle update=%s in chain using handler=%s",
                        update, handler), exception);
            }
        }

        return false;
    }

    private void setUserHandler(long chatId, UpdateHandler handler) {
        Application.getUserStorage().getUserByChatId(chatId).setHandler(handler);
        log.debug("Set handler={} for user with chatId={}", handler.toString(), chatId);
    }
}