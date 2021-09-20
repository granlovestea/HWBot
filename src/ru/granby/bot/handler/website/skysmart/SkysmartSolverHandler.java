package ru.granby.bot.handler.website.skysmart;

import com.google.gson.Gson;
import com.pengrad.telegrambot.model.Update;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.granby.Application;
import ru.granby.bot.BotContract;
import ru.granby.bot.handler.base.UpdateHandler;
import ru.granby.model.dto.skysmart.SkysmartJoinRoom;
import ru.granby.model.dto.skysmart.SkysmartLoadStep;
import ru.granby.model.entity.skysmart.SkysmartStep;
import ru.granby.utils.ImageUtils;
import ru.granby.web.auth.IncorrectCredentialsException;
import ru.granby.web.js.SkysmartSolverJSExecutor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

public class SkysmartSolverHandler extends UpdateHandler {
    private static final Logger log = LoggerFactory.getLogger(SkysmartSolverHandler.class);
    private static final String HOMEWORK_INVITE_URL_PATTERN = "edu.skysmart.ru/student/";
    private static final String HOMEWORK_ACTIVE_URL_PATTERN = "edu.skysmart.ru/lesson/homework/";
    private static final String JOIN_URL = "https://api-edu.skysmart.ru/api/v1/lesson/join";
    private static final String LOAD_STEP_URL = "https://api-edu.skysmart.ru/api/v1/content/step/load?stepUuid=";
    private static final String ROOM_HASH_KEY = "roomHash";
    private static final String SCREENSHOTS_PATH = System.getProperty("user.dir") + "\\logs\\skysmart\\screenshots\\";
    private static final long SOLVED_STEP_PAGE_LOAD_TIMEOUT = 5000;
    private String roomHash;
    private boolean solved;
    private Gson gson;
    private int currentStepIndex;
    private SkysmartSolverJSExecutor solverJSExecutor;

    public SkysmartSolverHandler(BotContract.View view, long userChatId) {
        super(view, userChatId);
        this.solved = false;
        this.gson = new Gson();
        this.solverJSExecutor = new SkysmartSolverJSExecutor((JavascriptExecutor) Application.getWebDriver());
    }

    @Override
    public boolean tryHandle(Update update) {
        String text = update.message().text();
        currentStepIndex = 0;

        Single.fromCallable(() -> getRoomHash(text))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(this::getStepsIds)
                .toObservable()
                .flatMapIterable(steps -> steps)
                .map(this::getStepWithContent)
                .map(this::getSolvedStep)
                .observeOn(Schedulers.single())
                .flatMapCompletable(solvedContent -> Completable.fromAction(() -> sendSolvedStep(solvedContent)))
                .subscribe(() -> {
                    view.showFinishedSkysmartRoom(userChatId);
                    view.startSkysmartHandler(userChatId);
                }, (t) -> {
                    if(t instanceof IncorrectHomeworkUrlException) {
                        log.warn("Exception during getRoomHash with messageText=" + text, t);
                        view.showIncorrectHomeworkUrl(userChatId);
                        view.startSkysmartHandler(userChatId);
                    } else if(t instanceof IncorrectCredentialsException) {
                        log.warn("Exception during getStepIds with messageText=" + text, t);
                        view.showTokenNotValid(userChatId);
                        view.startSkysmartHandler(userChatId);
                    } else {
                        log.warn("Unknown exception with messageText=" + text, t);
                        view.showInternalError(userChatId);
                        view.startSkysmartHandler(userChatId);
                    }
                });

        return true;
    }

    public boolean isSolved() {
        return solved;
    }

    private String getRoomHash(String messageText) {
        String roomHash;

        if(messageText.contains(HOMEWORK_ACTIVE_URL_PATTERN)) {
            String textAfterPattern = messageText.split(HOMEWORK_ACTIVE_URL_PATTERN)[1];
            if(textAfterPattern.contains("/")) {
                roomHash = textAfterPattern.substring(0, textAfterPattern.indexOf("/"));
            } else {
                roomHash = textAfterPattern;
            }

            this.roomHash = roomHash;
            return roomHash;
        }

        throw new IncorrectHomeworkUrlException("Passed messageText doesn't contain roomHash or invite room hash");
    }

    private List<String> getStepsIds(String roomHash) throws IOException {
        RequestBody joinBody = new FormBody.Builder()
                .add(ROOM_HASH_KEY, roomHash)
                .build();

        Request joinRequest = new Request.Builder()
                .url(JOIN_URL)
                .post(joinBody)
                .header("Authorization", "Bearer " + user.getSkysmartCredentials().getToken())
                .build();

        log.info("getStepsIds: Calling room " + roomHash);
        try (Response joinResponse = Application.getWebClient().newCall(joinRequest).execute()) {
            if(joinResponse.code() == 403 || joinResponse.code() == 401) {
                throw new IncorrectCredentialsException(String.format("Token is not valid anymore joinRequest=%s, joinResponse=%s user=%s", joinRequest, joinResponse, user));
            }

            // Sometimes stepUuids stored in taskMeta and sometimes in taskStudentMeta so we need to check both
            SkysmartJoinRoom joinRoom = gson.fromJson(joinResponse.body().string(), SkysmartJoinRoom.class);
            List<String> taskMetaStepsIds = joinRoom.getTaskMeta().getStepUuids();
            List<String> taskStudentMetaStepIds = joinRoom.getTaskStudentMeta().getSteps()
                    .stream()
                    .map(SkysmartStep::getStepUuid)
                    .collect(Collectors.toList());

            return taskMetaStepsIds.size() > taskStudentMetaStepIds.size() ? taskMetaStepsIds : taskStudentMetaStepIds;
        }
    }

    private SkysmartStep getStepWithContent(String stepId) throws IOException {
        Request loadStepRequest = new Request.Builder()
                .url(LOAD_STEP_URL + stepId)
                .get()
                .header("Authorization", "Bearer " + user.getSkysmartCredentials().getToken())
                .build();

        log.info("getStepContent: Loading step content with stepId=" + stepId);
        try (Response loadStepResponse = Application.getWebClient().newCall(loadStepRequest).execute()) {
            if(loadStepResponse.code() == 403 || loadStepResponse.code() == 401) {
                throw new IncorrectCredentialsException(String.format("Token is not valid anymore loadStepRequest=%s, loadStepResponse=%s user=%s", loadStepRequest, loadStepResponse, user));
            }

            return new SkysmartStep(stepId, gson.fromJson(loadStepResponse.body().string(), SkysmartLoadStep.class).getContent());
        }
    }

    private SkysmartStep getSolvedStep(SkysmartStep step) {
        solverJSExecutor.executeStepSolving(step);
        return step;
    }

    private void sendSolvedStep(SkysmartStep step) throws IOException {
        new WebDriverWait(Application.getWebDriver(), SOLVED_STEP_PAGE_LOAD_TIMEOUT).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

        String screenshotName = step.getStepUuid() + ".png";
        String screenshotPath = SCREENSHOTS_PATH + "\\" + userChatId + "\\" + roomHash;
        String screenshotFullPath = screenshotPath + "\\" + screenshotName;

        Files.createDirectories(Paths.get(screenshotPath));

        byte[] screenshotBytes = ((TakesScreenshot) Application.getWebDriver()).getScreenshotAs(OutputType.BYTES);
        Files.copy(new ByteArrayInputStream(screenshotBytes), Paths.get(screenshotPath + "\\" + screenshotName), StandardCopyOption.REPLACE_EXISTING);

        log.info(String.format("Made skysmart solved step screenshot at %s", screenshotFullPath));

        view.showSolvedSkysmartStepScreenshot(userChatId, screenshotBytes, currentStepIndex + 1, step.getStepUuid(), roomHash);
        currentStepIndex++;
    }
}