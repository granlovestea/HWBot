package ru.granby.bot;


import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.granby.bot.callback.handler.SupportButtonHandler;
import ru.granby.bot.handler.website.skysmart.SkysmartHandler;
import ru.granby.model.BotMessage;
import ru.granby.model.Config;

import java.io.IOException;
import java.util.concurrent.Executors;

public class Bot implements BotContract.View {
    public static final Logger log = LoggerFactory.getLogger(Bot.class);
    private BotContract.Presenter presenter;
    private TelegramBot telegramBot;
    private Scheduler telegramScheduler;

    public Bot() {
        presenter = new BotPresenter(this);
        telegramBot = new TelegramBot(Config.BOT_TOKEN);
        telegramScheduler = Schedulers.from(Executors.newFixedThreadPool(1));
    }

    @Override
    public void start() {
        telegramBot.setUpdatesListener(updates -> {
            updates.forEach(presenter::onUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

        log.info("Started bot");
    }

    @Override
    public Scheduler getTelegramScheduler() {
        return telegramScheduler;
    }

    @Override
    public void showTestMessage(long chatId, String message) {
        sendTextMessage(chatId, message);
    }

    @Override
    public void showAboutBot(long chatId) {
        sendTextMessage(chatId, BotMessage.ABOUT_BOT);
    }

    @Override
    public void showUnknownCommandError(long chatId) {
        sendTextMessage(chatId, BotMessage.UNKNOWN_COMMAND);
    }

    @Override
    public void showChooseWebsiteMenu(long chatId) {
        String text = BotMessage.CHOOSE_HOMEWORK_WEBSITE;

        Keyboard websiteMenuKeyboard = new ReplyKeyboardMarkup(
                new String[]{SkysmartHandler.name, "Imumk (Pysicon)"},
                new String[]{"Resh", "..."})
                .oneTimeKeyboard(true)
                .resizeKeyboard(true);

        SendMessage sendMessage = new SendMessage(chatId, text);
        sendMessage.replyMarkup(websiteMenuKeyboard);

        telegramBot.execute(sendMessage, new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage sendMessage, SendResponse sendResponse) {
                log.info(String.format("showChooseWebsiteMenu: chatId=%s text=%s websiteMenuKeyboard=%s"
                                + " sendMessage=%s sendResponse=%s", chatId, text,
                        websiteMenuKeyboard.toString(), sendMessage.toString(), sendResponse.toString()));
                presenter.setChooseWebsiteHandler(chatId);
            }

            @Override
            public void onFailure(SendMessage sendMessage, IOException e) {
                logExecutionError(chatId, sendMessage, e);
            }
        });
    }

    @Override
    public void showUnknownWebsite(long chatId) {
        sendTextMessage(chatId, BotMessage.UNKNOWN_WEBSITE);
    }

    @Override
    public void startSkysmartHandler(long chatId) {
        presenter.startSkysmartHandler(chatId);
    }

    @Override
    public void showNeedAuth(long chatId, String website) {
        sendTextMessage(chatId, String.format(BotMessage.NEED_AUTH, website));
    }

    @Override
    public void showNeedLogin(long chatId) {
        sendTextMessage(chatId, BotMessage.NEED_LOGIN);
    }

    @Override
    public void showNeedPassword(long chatId) {
        sendTextMessage(chatId, BotMessage.NEED_PASSWORD);
    }

    @Override
    public void showAuthInProgress(long chatId) {
        sendTextMessage(chatId, BotMessage.AUTH_IN_PROGRESS);
    }

    @Override
    public void showIncorrectCredentials(long chatId) {
        sendTextMessage(chatId, BotMessage.INCORRECT_CREDENTIALS);
    }

    @Override
    public void showInternalError(long chatId) {
        sendTextMessage(chatId, BotMessage.INTERNAL_ERROR);
    }

    @Override
    public void showAuthSuccess(long chatId) {
        sendTextMessage(chatId, BotMessage.AUTH_SUCCESS);
    }

    @Override
    public void showTryAuthExistingAccount(long chatId) {
        sendTextMessage(chatId, BotMessage.TRY_AUTH_EXISTING_ACCOUNT);
    }

    @Override
    public void startEmptyHandler(long chatId) {
        presenter.setEmptyHandler(chatId);
        sendTextMessage(chatId, "Set empty handler. Test message");
    }

    @Override
    public void showAuthLastCredentialsSuccess(long chatId) {
        sendTextMessage(chatId, BotMessage.AUTH_LAST_CREDENTIALS_SUCCESS);
    }

    @Override
    public void showNeedSkysmartHomeworkUrl(long chatId) {
        sendTextMessage(chatId, BotMessage.NEED_SKYSMART_HOMEWORK_URL);
    }

    @Override
    public void showNotYetSupported(long chatId) {
        sendTextMessage(chatId, BotMessage.NOT_YET_SUPPORTED);
    }

    @Override
    public void showIncorrectHomeworkUrl(long chatId) {
        sendTextMessage(chatId, BotMessage.SHOW_INCORRECT_HOMEWORK_URL);
        sendTextMessage(chatId, BotMessage.NEED_SKYSMART_HOMEWORK_URL);
    }

    @Override
    public void showTokenNotValid(long chatId) {
        sendTextMessage(chatId, BotMessage.TOKEN_NOT_VALID);
    }

    @Override
    public void showFinishedSkysmartRoom(long userChatId) {
        sendTextMessage(userChatId, BotMessage.FINISHED_SKYSMART_ROOM);
    }

    @Override
    public void showSolvedSkysmartStepScreenshot(long userChatId, byte[] screenshotBytes, int stepNum, String stepId, String roomHash) {
        sendImageTextMessage(userChatId, screenshotBytes, String.format(BotMessage.SOLVED_SKYSMART_STEP_SCREENSHOT,
                stepNum, roomHash, stepId));
    }

    private void sendTextMessage(long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text),
                new Callback<SendMessage, SendResponse>() {
                    @Override
                    public void onResponse(SendMessage sendMessage, SendResponse sendResponse) {
                        log.info(String.format("sendTextMessage: chatId=%s text=%s sendMessage=%s sendResponse=%s", chatId, text, sendMessage, sendResponse));
                    }

                    @Override
                    public void onFailure(SendMessage sendMessage, IOException e) {
                        logExecutionError(chatId, sendMessage, e);
                    }
                });
    }

    private void sendImageTextMessage(long chatId, byte[] image, String text) {
        telegramBot.execute(new SendPhoto(chatId, image).caption(text), new Callback<SendPhoto, SendResponse>() {
            @Override
            public void onResponse(SendPhoto sendPhoto, SendResponse sendResponse) {
                log.info(String.format("sendTextMessage: chatId=%s text=%s sendMessage=%s sendResponse=%s", chatId, text, sendPhoto, sendResponse));
            }

            @Override
            public void onFailure(SendPhoto sendPhoto, IOException e) {
                logExecutionError(chatId, sendPhoto, e);
            }
        });
    }

    private void logExecutionError(long chatId, AbstractSendRequest sendRequest, IOException e) {
        log.error(String.format("Error during sending message chatId=%s sendRequest=%s", chatId, sendRequest), e);
    }

    private InlineKeyboardMarkup getSupportButtonMarkup() {
        return new InlineKeyboardMarkup(new InlineKeyboardButton("Поддержка")
                .callbackData(SupportButtonHandler.CALLBACK_DATA));
    }
}