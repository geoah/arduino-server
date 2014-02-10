package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.auth.User;
import com.ddumanskiy.arduino.common.Command;
import com.ddumanskiy.arduino.common.message.MobileClientMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

/**
 * User: ddumanskiy
 * Date: 11/6/13
 * Time: 8:29 PM
 */
public class LoadProfileHandler extends BaseSimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(LoadProfileHandler.class);

    private static final byte[] ALLOWED_COMMANDS = new byte[] {
            Command.LOAD_PROFILE,
    };

    @Override
    protected byte[] getHandlerCommands() {
        return ALLOWED_COMMANDS;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel incomeChannel = e.getChannel();

        if (!isHandlerCommand(e.getMessage())) {
            ctx.sendUpstream(e);
            return;
        }

        MobileClientMessage message = (MobileClientMessage) e.getMessage();

        User authUser = Session.getChannelToken().get(incomeChannel.getId());

        message.setBody(authUser.getUserProfile() == null ? "{}" : authUser.getUserProfile().toString());
        incomeChannel.write(message);
    }

}