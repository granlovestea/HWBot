package ru.granby.model.entity;

import ru.granby.bot.handler.base.UpdateHandler;

import java.util.Objects;

public class User {
    private String username;
    private UpdateHandler handler;
    private Credentials skysmartCredentials;

    public User(String username, UpdateHandler handler) {
        this.username = username;
        this.handler = handler;
        this.skysmartCredentials = new Credentials();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(handler, user.handler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, handler);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", handler=" + handler +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UpdateHandler getHandler() {
        return handler;
    }

    public void setHandler(UpdateHandler handler) {
        this.handler = handler;
    }

    public Credentials getSkysmartCredentials() {
        return skysmartCredentials;
    }

    public void setSkysmartCredentials(Credentials skysmartCredentials) {
        this.skysmartCredentials = skysmartCredentials;
    }
}
