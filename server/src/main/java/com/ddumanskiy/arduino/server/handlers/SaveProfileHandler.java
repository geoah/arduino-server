package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.auth.UserRegistry;
import com.ddumanskiy.arduino.model.UserProfile;
import com.ddumanskiy.arduino.user.User;
import com.ddumanskiy.arduino.utils.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.*;

import static com.ddumanskiy.arduino.response.ResponseCode.INVALID_COMMAND_FORMAT;
import static com.ddumanskiy.arduino.response.ResponseCode.OK;

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
            incomeChannel.write(INVALID_COMMAND_FORMAT);
            return;
        }

        String userProfileString = messageParts[1];

        log.info("Trying to parse user profile : {}", userProfileString);
        UserProfile userProfile = JsonParser.parse(userProfileString);
        if (userProfile == null) {
            log.error("Register Handler. Wrong user profile message format.");
            incomeChannel.write(INVALID_COMMAND_FORMAT);
            return;
        }

        log.info("Trying save user profile.");

        User authUser = Session.getChannelToken().get(incomeChannel.getId());
        if (authUser == null) {
            log.error("Channel not authorized. Send login first. Closing socket.");
            incomeChannel.close();
            return;
        }

        authUser.setUserProfile(userProfile);
        UserRegistry.save();

        incomeChannel.write(OK);
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