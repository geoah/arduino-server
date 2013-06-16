package com.ddumanskiy.arduino.server;


import org.jboss.netty.channel.group.DefaultChannelGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 11:20 PM
 */
public class GroupHolder {

    private static Map<String, DefaultChannelGroup> privateRooms = new ConcurrentHashMap<String, DefaultChannelGroup>();

    public static Map<String, DefaultChannelGroup> getPrivateRooms() {
        return privateRooms;
    }

}
