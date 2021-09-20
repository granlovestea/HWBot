package ru.granby.web.auth;

import com.google.gson.Gson;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.granby.Application;
import ru.granby.model.dto.skysmart.SkysmartAuthResponse;
import ru.granby.model.entity.Credentials;
import ru.granby.model.entity.User;

import java.io.IOException;

public class SkysmartAuth extends WebsiteAuth {
    private static final Logger log = LoggerFactory.getLogger(SkysmartAuth.class);
    public static final long AUTH_TIMEOUT = 30000; // ms
    private static final String AUTH_URL = "https://api-edu.skysmart.ru/api/v2/auth/auth/student";
    private static final String CHECK_TOKEN_URL = "https://api-edu.skysmart.ru/api/v1/student/task/finished";
    private Gson gson;

    public SkysmartAuth(User user, long userChatId) {
        super(user, userChatId);
        this.gson = new Gson();
    }

    public void auth(String login, String password) {
        User user = Application.getUserStorage().getUserByChatId(userChatId);

        RequestBody authBody = new FormBody.Builder()
                .add("phoneOrEmail", login)
                .add("password", password)
                .build();
        Request authRequest = new Request.Builder()
                .url(AUTH_URL)
                .post(authBody)
                .build();

        try (Response rawResponse = Application.getWebClient().newCall(authRequest).execute()) {
            log.debug(String.format("Auth call execution response=%s", rawResponse.toString()));

            if(rawResponse.code() == 403) {
                throw new IncorrectCredentialsException("Code 403: forbidden. Incorrect login or password");
            }

            SkysmartAuthResponse response = gson.fromJson(rawResponse.body().string(), SkysmartAuthResponse.class);
            log.debug("Parsed response: " + response.toString());

            if(!response.isSuccessLogin()) {
                throw new IncorrectCredentialsException("isSuccessLogin=false, probably incorrect credentials");
            }

            String token = response.getJwtToken();
            user.setSkysmartCredentials(new Credentials(login, password, token));
            Application.getUserStorage().updateUser(userChatId, user);
        } catch (IOException | NullPointerException e) {
            throw new IllegalStateException("Error during auth call execution", e);
        }
    }

    @Override
    protected boolean checkToken() {
        String token = user.getSkysmartCredentials().getToken();
        if(token == null) return false;

        Request checkTokenRequest = new Request.Builder()
                .url(CHECK_TOKEN_URL)
                .get()
                .header("Authorization", token)
                .build();

        try {
            Response rawResponse = Application.getWebClient().newCall(checkTokenRequest).execute();
            log.debug(String.format("Check token call execution request=%s response=%s",
                    checkTokenRequest, rawResponse.toString()));

            if(rawResponse.isSuccessful()) {
                return true;
            } else {
                Application.getUserStorage().getUserByChatId(userChatId).getSkysmartCredentials().setToken(null);
            }
        } catch (IOException e) {
            log.warn("Exception occurred during check token call", e);
        }

        return false;
    }

    @Override
    protected boolean checkLoginPassword() {
        String login = user.getSkysmartCredentials().getLogin();
        String password = user.getSkysmartCredentials().getPassword();

        if(login == null || password == null) return false;

        try {
            auth(login, password);
            return true;
        } catch (Exception e) {
            log.warn("Exception occurred during login & password check", e);
            return false;
        }
    }
}
