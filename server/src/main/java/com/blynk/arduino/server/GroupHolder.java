package com.blynk.arduino.server;


import com.blynk.arduino.auth.User;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 11:20 PM
 */
public class GroupHolder {

    private static Map<User, DefaultChannelGroup> privateRooms = new ConcurrentHashMap<>();

    public static Map<User, DefaultChannelGroup> getPrivateRooms() {
        return privateRooms;
    }

}
