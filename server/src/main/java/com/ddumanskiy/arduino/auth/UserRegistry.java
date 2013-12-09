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

    //init user DB if possible
    static {
        users = FileManager.deserialize();
    }

    public static boolean isUserExists(String name) {
        return users.get(name) != null;
    }

    public static User getByName(String name) {
        return users.get(name);
    }

    public static User createNewUser(String userName, String pass) {
        String id = UUID.randomUUID().toString();
        User newUser = new User(userName, pass, id);
        users.putIfAbsent(userName, newUser);

        //todo, yes this not optimal solution, but who cares?
        //todo this may be moved to separate thread
        FileManager.saveNewUserToFile(newUser);

        MailTLS.sendMail(userName, "You just registered to Arduino control.", newUser.getId());

        return newUser;
    }

}
