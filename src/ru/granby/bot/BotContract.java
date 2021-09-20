package ru.granby.bot;


import com.pengrad.telegrambot.model.Update;
import io.reactivex.rxjava3.core.Scheduler;
import ru.granby.model.entity.skysmart.SkysmartStep;

public interface BotContract {
    interface View {
        void start();
        Scheduler getTelegramScheduler();
        void showTestMessage(long chatId, String message);
        void showAboutBot(long chatId);
        void showUnknownCommandError(long chatId);
        void showChooseWebsiteMenu(long chatId);
        void showUnknownWebsite(long chatId);
        void startSkysmartHandler(long chatId);
        void showNeedAuth(long chatId, String website);
        void showNeedLogin(long chatId);
        void showNeedPassword(long chatId);
        void showAuthInProgress(long chatId);
        void showIncorrectCredentials(long chatId);
        void showInternalError(long chatId);
        void showAuthSuccess(long chatId);
        void showTryAuthExistingAccount(long chatId);
        void startEmptyHandler(long chatId);
        void showAuthLastCredentialsSuccess(long chatId);
        void showNeedSkysmartHomeworkUrl(long chatId);
        void showNotYetSupported(long chatId);
        void showIncorrectHomeworkUrl(long chatId);
        void showTokenNotValid(long chatId);
        void showFinishedSkysmartRoom(long userChatId);
        void showSolvedSkysmartStepScreenshot(long userChatId, byte[] screenshotBytes, int i, String stepUuid, String roomHash);
    }

    interface Presenter {
        void onUpdate(Update update);
        void setChooseWebsiteHandler(long chatId);
        void startSkysmartHandler(long chatId);
        void setEmptyHandler(long chatId);
    }
}