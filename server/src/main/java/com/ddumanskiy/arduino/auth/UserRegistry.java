package com.ddumanskiy.arduino.auth;

import com.ddumanskiy.arduino.mail.MailTLS;
import com.ddumanskiy.arduino.utils.FileManager;

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

    private UserRegistry() {

    }

    private static final ConcurrentHashMap<String, User> users;
    private static final ConcurrentHashMap<String, User> userTokens;

    //init user DB if possible
    static {
        users = FileManager.deserialize();
        userTokens = new ConcurrentHashMap<>(users.size());
        for (User user : users.values()) {
            userTokens.put(user.getId(), user);
            TimerRegistry.checkUserHasTimers(user);
        }
    }

    public static boolean isUserExists(String name) {
        return users.get(name) != null;
    }

    public static User getByName(String name) {
        return users.get(name);
    }

    public static User getByToken(String token) {
        return userTokens.get(token);
    }

    public static User createNewUser(String userName, String pass) {
        String id = UUID.randomUUID().toString().replace("-", "");
        User newUser = new User(userName, pass, id);

        users.put(userName, newUser);
        userTokens.put(newUser.getId(), newUser);

        //todo, yes this not optimal solution, but who cares?
        //todo this may be moved to separate thread
        FileManager.saveNewUserToFile(newUser);

        MailTLS.sendMail(userName, "You just registered to Arduino control.", newUser.getId());

        return newUser;
    }

}
