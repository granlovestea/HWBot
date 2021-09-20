package ru.granby.bot.handler.website.skysmart;

import com.pengrad.telegrambot.model.Update;
import org.openqa.selenium.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.granby.Application;
import ru.granby.bot.BotContract;
import ru.granby.bot.handler.base.UpdateHandler;
import ru.granby.bot.handler.website.AuthHandler;
import ru.granby.web.auth.SkysmartAuth;

public class SkysmartHandler extends UpdateHandler {
    private static final Logger log = LoggerFactory.getLogger(SkysmartHandler.class);
    public static final String name = "Skysmart";
    private AuthHandler authHandler;
    private SkysmartSolverHandler skysmartSolverHandler;
    private SkysmartState state;

    public SkysmartHandler(BotContract.View view, long userChatId) {
        super(view, userChatId);
        this.authHandler = new AuthHandler(view, userChatId, name, new SkysmartAuth(user, userChatId), this::onAuthFinished);
        this.state = SkysmartState.NEED_AUTH;
        this.skysmartSolverHandler = new SkysmartSolverHandler(view, userChatId);
    }

    @Override
    public boolean tryHandle(Update update) {
        if(!authHandler.isAuthed()) return authHandler.tryHandle(update);
        if(!skysmartSolverHandler.isSolved()) return skysmartSolverHandler.tryHandle(update);

        if(state == SkysmartState.NEED_HOMEWORK_URL) askHomeworkUrl();

        return true;
    }

    public void start() {
        authHandler.start();
    }

    private void onAuthFinished() {
        //setSkysmartAuthToken(); Don't need it until not opening skysmart in webdriver
        askHomeworkUrl();
    }

    private void setSkysmartAuthToken() {
        Application.getWebDriver().get("https://skysmart.ru");
        Application.getWebDriver().manage().addCookie(
                new Cookie.Builder("token_edu_skysmart", user.getSkysmartCredentials().getToken())
                        .domain("skysmart.ru")
                        .isHttpOnly(true)
                        .isSecure(true)
                        .path("/")
                        .build());
    }

    private void askHomeworkUrl() {
        state = SkysmartState.NEED_HOMEWORK_URL;
        view.showNeedSkysmartHomeworkUrl(userChatId);
    }
}