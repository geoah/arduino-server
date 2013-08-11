package com.ddumanskiy.arduino.server;


import com.ddumanskiy.arduino.user.User;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 11:20 PM
 */
public class GroupHolder {

    private static Map<User, DefaultChannelGroup> privateRooms = new ConcurrentHashMap<User, DefaultChannelGroup>();

    public static Map<User, DefaultChannelGroup> getPrivateRooms() {
        return privateRooms;
    }

}
