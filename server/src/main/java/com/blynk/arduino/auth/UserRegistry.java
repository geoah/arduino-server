package com.blynk.arduino.auth;

import com.blynk.arduino.utils.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for holding info regarding registered users and profiles.
 *
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 4:02 PM
 */
public final class UserRegistry {

    private static final Logger log = LogManager.getLogger(UserRegistry.class);

    private UserRegistry() {

    }

    private static final ConcurrentHashMap<String, User> users;

    //init user DB if possible
    static {
        users = FileManager.deserialize();
        for (User user : users.values()) {
            TimerRegistry.checkUserHasTimers(user);
        }
    }

    public static boolean isUserExists(String name) {
        return users.get(name) != null;
    }

    public static User getByName(String name) {
        return users.get(name);
    }

    //todo optimize
    public static User getByToken(String token) {
        for (User user : users.values()) {
            for (String userToken : user.getDashTokens().values()) {
                if (userToken.equals(token)) {
                    return user;
                }
            }
        }
        return null;
    }

    public static String getToken(User user, Long dashboardId) {
        Map<Long, String> dashTokens = user.getDashTokens();
        String token = dashTokens.get(dashboardId);

        //if token not exists. generate new one
        if (token == null) {
            log.info("Token for user {} and dashId {} not generated yet.", user.getName(), dashboardId);
            token = generateNewToken();
            log.info("Generated token for user {} and dashId {} is {}.", user.getName(), dashboardId, token);
            user.getDashTokens().put(dashboardId, token);
            FileManager.overrideUserFile(user);
        } else {
            log.info("Token for user {} and dashId {} generated already. Token {}", user.getName(), dashboardId, token);
        }

        return token;
    }

    private static String generateNewToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static User createNewUser(String userName, String pass) {
        User newUser = new User(userName, pass);

        users.put(userName, newUser);

        //todo, yes this not optimal solution, but who cares?
        //todo this may be moved to separate thread
        FileManager.saveNewUserToFile(newUser);
        return newUser;
    }

}
