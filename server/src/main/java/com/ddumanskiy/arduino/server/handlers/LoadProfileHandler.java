package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.common.message.Message;
import com.ddumanskiy.arduino.response.ResponseCode;
import com.ddumanskiy.arduino.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.*;

/**
 * User: ddumanskiy
 * Date: 11/6/13
 * Time: 8:29 PM
 */
public class LoadProfileHandler extends SimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(LoadProfileHandler.class);

    private static final String LOAD_PROFILE_TOKEN = "loadProfile";

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel incomeChannel = e.getChannel();
        Message message = (Message) e.getMessage();

        if (!isLoadProfileAction(message.getBody())) {
            ctx.sendUpstream(e);
            return;
        }

        User authUser = Session.getChannelToken().get(incomeChannel.getId());
        if (authUser == null) {
            log.error("Channel not authorized. Send login first. Closing socket.");
            message.setBody(ResponseCode.USER_NOT_AUTHENTICATED);
            incomeChannel.write(message);
            return;
        }

        message.setBody(authUser.getUserProfile() == null ? "{}" : authUser.getUserProfile().toString());
        incomeChannel.write(message);
    }

    private boolean isLoadProfileAction(String actionName) {
        return LOAD_PROFILE_TOKEN.equalsIgnoreCase(actionName);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        log.error("Error in {}", this.getClass().getName());
        log.error(e.getCause());

        Channel ch = e.getChannel();
        ch.close();
    }
}