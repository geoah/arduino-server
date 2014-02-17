package com.blynk.arduino.server.handlers;

import com.blynk.arduino.common.enums.Command;
import com.blynk.arduino.common.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * User: ddumanskiy
 * Date: 08.12.13
 * Time: 12:05
 */
public abstract class BaseSimpleChannelHandler extends SimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(BaseSimpleChannelHandler.class);

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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        log.error(e + ". Handler : " + ctx.getHandler());
    }

}
