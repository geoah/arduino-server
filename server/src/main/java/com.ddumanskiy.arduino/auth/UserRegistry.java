package com.ddumanskiy.arduino.auth;

import com.ddumanskiy.arduino.user.User;
import com.ddumanskiy.arduino.utils.Serializer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for holding info regarding registered users and their password.
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
        users = Serializer.deserialize();
    }



    public static boolean isUserExists(String name) {
        return users.get(name) != null;
    }

    public static User getByName(String name) {
        return users.get(name);
    }

    public static void save() {
        Serializer.serialize(users);
    }

    public static void createNewUser(String name, String pass, String id) {
        User newUser = new User(name, pass, id);
        users.putIfAbsent(name, newUser);

        //todo, yes this not optimal solution, but who cares?
        //todo this may be moved to separate thread
        Serializer.serialize(users);
    }

}
