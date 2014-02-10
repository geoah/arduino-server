package com.blynk.arduino.utils;

import com.blynk.arduino.server.handlers.enums.ChannelType;
import org.jboss.netty.channel.Channel;

/**
 * User: ddumanskiy
 * Date: 2/10/14
 * Time: 9:26 PM
 */
public class ChannelsUtils {

    public static boolean isArduinoChannel(Channel channel) {
        return channel.getAttachment() != null && (channel.getAttachment() == ChannelType.ARDUINO);
    }

    public static boolean isMobileChannel(Channel channel) {
        return channel.getAttachment() == null || (channel.getAttachment() == ChannelType.MOBILE_CLIENT);
    }

}
