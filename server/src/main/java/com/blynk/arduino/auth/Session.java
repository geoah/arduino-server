package com.blynk.arduino.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: ddumanskiy
 * Date: 6/16/13
 * Time: 9:52 AM
 */
public class Session {

    private static Map<Integer, User> channelToken = new ConcurrentHashMap<>();

    public static Map<Integer, User> getChannelToken() {
        return channelToken;
    }
}
