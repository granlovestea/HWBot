package ru.granby.model.dto.skysmart;

import com.google.gson.annotations.SerializedName;

public class SkysmartAuthResponse {
    @SerializedName("isSuccessLogin")
    private boolean isSuccessLogin;

    @SerializedName("jwtToken")
    private String jwtToken;

    @SerializedName("userHash")
    private String userHash;

    public SkysmartAuthResponse(boolean isSuccessLogin, String jwtToken, String userHash) {
        this.isSuccessLogin = isSuccessLogin;
        this.jwtToken = jwtToken;
        this.userHash = userHash;
    }

    @Override
    public String toString() {
        return "SkysmartAuthResponse{" +
                "isSuccessLogin=" + isSuccessLogin +
                ", jwtToken='" + jwtToken + '\'' +
                ", userHash='" + userHash + '\'' +
                '}';
    }

    public boolean isSuccessLogin() {
        return isSuccessLogin;
    }

    public void setSuccessLogin(boolean successLogin) {
        isSuccessLogin = successLogin;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getUserHash() {
        return userHash;
    }

    public void setUserHash(String userHash) {
        this.userHash = userHash;
    }
}
