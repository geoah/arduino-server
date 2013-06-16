package com.ddumanskiy.arduino.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: ddumanskiy
 * Date: 6/16/13
 * Time: 9:52 AM
 */
public class Authentification {

    private static Map<Integer, String> channelToken = new ConcurrentHashMap<Integer, String>();

    //todo verify if it is an auth token by some rule
    public static boolean isAuthToken(String authToken) {
        boolean result = authToken.startsWith("auth_");
        System.out.println("Is message AUTH token? Result - " + result);
        return result;
    }

    public static boolean isRegisteredUser(String authToken) {
        System.out.println("Is registered User? Result - true");
        return true;
    }


    public static Map<Integer, String> getChannelToken() {
        return channelToken;
    }
}
