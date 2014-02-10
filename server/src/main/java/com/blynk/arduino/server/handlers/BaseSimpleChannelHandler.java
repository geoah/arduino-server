package com.blynk.arduino.server.handlers;

import com.blynk.arduino.common.enums.Command;
import com.blynk.arduino.common.message.Message;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * User: ddumanskiy
 * Date: 08.12.13
 * Time: 12:05
 */
public abstract class BaseSimpleChannelHandler extends SimpleChannelHandler {

    protected abstract Command[] getHandlerCommands();

    protected boolean isHandlerCommand(Object msg) {
        return isHandlerCommand(((Message) msg).getCommand());
    }

    protected boolean isHandlerCommand(byte inputCommand) {
        Command inputCommandEnum = Command.getByCode(inputCommand);
        for (Command allowedCommand : getHandlerCommands()) {
            if (inputCommandEnum == allowedCommand) {
                return true;
            }
        }
        return false;
    }

}
