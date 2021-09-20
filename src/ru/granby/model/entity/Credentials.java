package ru.granby.model.entity;

public class Credentials {
    private static final String TOKEN_PREFIX = "Bearer ";
    private String login;
    private String password;
    private String token;

    public Credentials(String login, String password, String token) {
        this.login = login;
        this.password = password;
        this.token = token;
    }

    public Credentials(String token) {
        this.token = token;
    }

    public Credentials() {

    }

    @Override
    public String toString() {
        return "Credentials{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
