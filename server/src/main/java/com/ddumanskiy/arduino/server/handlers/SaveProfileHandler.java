package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.auth.UserRegistry;
import com.ddumanskiy.arduino.common.message.Message;
import com.ddumanskiy.arduino.model.UserProfile;
import com.ddumanskiy.arduino.user.User;
import com.ddumanskiy.arduino.utils.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.*;

import static com.ddumanskiy.arduino.response.ResponseCode.*;

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
        Message message = (Message) e.getMessage();

        String[] messageParts = message.getBody().split(" ");

        if (!isSaveProfileAction(messageParts[0])) {
            ctx.sendUpstream(e);
            return;
        }

        //expecting message with 2 parts
        if (messageParts.length != 2) {
            log.error("Register Handler. Wrong income message format.");
            message.setBody(INVALID_COMMAND_FORMAT);
            incomeChannel.write(message);
            return;
        }

        String userProfileString = messageParts[1];

        log.info("Trying to parse user profile : {}", userProfileString);
        UserProfile userProfile = JsonParser.parse(userProfileString);
        if (userProfile == null) {
            log.error("Register Handler. Wrong user profile message format.");
            message.setBody(INVALID_COMMAND_FORMAT);
            incomeChannel.write(message);
            return;
        }

        log.info("Trying save user profile.");

        User authUser = Session.getChannelToken().get(incomeChannel.getId());
        if (authUser == null) {
            log.error("Channel not authorized. Send login first. Closing socket.");
            message.setBody(USER_NOT_AUTHENTICATED);
            incomeChannel.write(message);
            return;
        }

        authUser.setUserProfile(userProfile);
        UserRegistry.save();

        message.setBody(OK);
        incomeChannel.write(message);
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