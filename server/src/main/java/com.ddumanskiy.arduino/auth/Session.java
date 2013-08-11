package com.ddumanskiy.arduino.auth;

import com.ddumanskiy.arduino.user.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: ddumanskiy
 * Date: 6/16/13
 * Time: 9:52 AM
 */
public class Session {

    private static Map<Integer, User> channelToken = new ConcurrentHashMap<Integer, User>();

    public static Map<Integer, User> getChannelToken() {
        return channelToken;
    }
}
