package com.blynk.arduino.server.handlers;

import com.blynk.arduino.common.message.Message;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * User: ddumanskiy
 * Date: 08.12.13
 * Time: 12:05
 */
public abstract class BaseSimpleChannelHandler extends SimpleChannelHandler {

    protected abstract byte[] getHandlerCommands();

    protected boolean isHandlerCommand(Object msg) {
        return isHandlerCommand(((Message) msg).getCommand());
    }

    protected boolean isHandlerCommand(byte inputCommand) {
        for (byte allowedCommand : getHandlerCommands()) {
            if (inputCommand == allowedCommand) {
                return true;
            }
        }
        return false;
    }

}
