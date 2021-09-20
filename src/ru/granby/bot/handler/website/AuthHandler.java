package ru.granby.bot.handler.website;

import com.pengrad.telegrambot.model.Update;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.granby.bot.BotContract;
import ru.granby.bot.handler.base.UpdateHandler;
import ru.granby.web.auth.IncorrectCredentialsException;
import ru.granby.web.auth.SkysmartAuth;
import ru.granby.web.auth.WebsiteAuth;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AuthHandler extends UpdateHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthHandler.class);
    private String websiteName;
    private WebsiteAuth websiteAuth;
    private AuthState authState;
    private String login;
    private String password;
    private boolean authed;
    private Runnable onAuthFinished;

    public AuthHandler(BotContract.View view, long userChatId, String websiteName, WebsiteAuth websiteAuth, Runnable onAuthFinished) {
        super(view, userChatId);
        this.websiteName = websiteName;
        this.websiteAuth = websiteAuth;
        this.authed = false;
        this.onAuthFinished = onAuthFinished;
    }

    @Override
    public boolean tryHandle(Update update) {
        String text = update.message().text();

        if(authState == AuthState.NEED_LOGIN) {
            login = text;
            authState = AuthState.NEED_PASSWORD;
            view.showNeedPassword(userChatId);
        } else if (authState == AuthState.NEED_PASSWORD){
            password = text;
            auth();
        }

        return true;
    }

    public void start() {
        Single.fromCallable(() -> websiteAuth.tryAuthExistingAccount())
                .subscribeOn(Schedulers.io())
                .observeOn(view.getTelegramScheduler())
                .timeout(SkysmartAuth.AUTH_TIMEOUT, TimeUnit.MILLISECONDS)
                .subscribe((authed) -> {
                    if(authed) {
                        view.showAuthLastCredentialsSuccess(userChatId);
                        finishAuth();
                    } else {
                        askCredentials();
                    }
                });
    }

    private void askCredentials() {
        authState = AuthState.NEED_LOGIN;
        view.showNeedAuth(userChatId, websiteName);
        view.showNeedLogin(userChatId);
    }


    private void auth() {
        view.showAuthInProgress(userChatId);
        Completable.fromAction(() -> websiteAuth.auth(login, password))
                .subscribeOn(Schedulers.io())
                .observeOn(view.getTelegramScheduler())
                .timeout(SkysmartAuth.AUTH_TIMEOUT, TimeUnit.MILLISECONDS)
                .subscribe(() -> {
                    log.info(String.format("Auth success for chatId=%s login=%s password=%s",
                            userChatId, login, password));
                    view.showAuthSuccess(userChatId);
                    finishAuth();
                }, (t) -> {
                    if(t instanceof IncorrectCredentialsException) {
                        log.warn(String.format("Incorrect credentials for chatId=%s with login=%s and password=%s",
                                userChatId, login, password));
                        view.showIncorrectCredentials(userChatId);
                        start();
                    } else if(t instanceof TimeoutException) {
                        log.error(String.format("Timeout during auth chatId=%s login=%s password=%s",
                                userChatId, login, password));
                        view.showInternalError(userChatId);
                    } else {
                        log.error(String.format(
                                "Unknown exception occurred during auth for chatId=%s with login=%s and password=%s",
                                userChatId, login, password), t);
                        view.showInternalError(userChatId);
                    }
                });
    }

    private void finishAuth() {
        authed = true;
        onAuthFinished.run();
    }

    public boolean isAuthed() {
        return authed;
    }
}
