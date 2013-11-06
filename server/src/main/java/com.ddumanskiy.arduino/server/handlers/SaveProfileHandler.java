package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.auth.UserRegistry;
import com.ddumanskiy.arduino.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.*;

import static com.ddumanskiy.arduino.common.Consts.BAD_RESPONSE;
import static com.ddumanskiy.arduino.common.Consts.OK_RESPONSE;

/**
 * User: ddumanskiy
 * Date: 11/6/13
 * Time: 8:29 PM
 */
public class SaveProfileHandler extends SimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(SaveProfileHandler.class);

    private static final String SAVE_PROFILE_TOKEN = "saveProfile";

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel incomeChannel = e.getChannel();
        String message = (String) e.getMessage();

        String[] messageParts = message.split(" ");

        if (!isSaveProfileAction(messageParts[0])) {
            ctx.sendUpstream(e);
            return;
        }

        //expecting message with 2 parts
        if (messageParts.length != 2) {
            log.error("Register Handler. Wrong income message format.");
            incomeChannel.write(BAD_RESPONSE);
            return;
        }

        String userProfile = messageParts[1];

        log.info("Trying save user : {}", userProfile);

        User authUser = Session.getChannelToken().get(incomeChannel.getId());
        if (authUser == null) {
            log.error("Channel not authorized. Send login first. Closing socket.");
            incomeChannel.close();
            return;
        }

        authUser.setData(userProfile);
        UserRegistry.save();

        incomeChannel.write(OK_RESPONSE);
    }

    private boolean isSaveProfileAction(String actionName) {
        return SAVE_PROFILE_TOKEN.equalsIgnoreCase(actionName);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
       log.error("Error in {}", this.getClass().getName());
        log.error(e.getCause());

        Channel ch = e.getChannel();
        ch.close();
    }
}