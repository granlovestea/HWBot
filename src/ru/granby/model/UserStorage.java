package ru.granby.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.granby.model.entity.User;

import java.util.*;

// TODO: replace with db
public class UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserStorage.class);
    private Map<Long, User> usersByChatIds;

    public UserStorage() {
        this.usersByChatIds = new HashMap<>();
    }

    public boolean tryAddUser(Long chatId, User user) {
        boolean isNewUser = usersByChatIds.containsKey(chatId);
        if(!isNewUser) {
            usersByChatIds.put(chatId, user);
            log.info("Added new user={} with chatId={}", user.toString(), chatId);
        }
        return isNewUser;
    }

    public User getUserByChatId(long chatId) {
        if(usersByChatIds.containsKey(chatId)) {
            return usersByChatIds.get(chatId);
        }
        throw new NoSuchElementException("Can't find user with chatId=" + chatId);
    }

    public void updateUser(long chatId, User user) {
        usersByChatIds.put(chatId, user);
    }
}
