package com.blynk.arduino.server.handlers;


import com.blynk.arduino.auth.Session;
import com.blynk.arduino.auth.User;
import com.blynk.arduino.common.enums.Command;
import com.blynk.arduino.common.enums.Response;
import com.blynk.arduino.common.message.MobileClientMessage;
import com.blynk.arduino.common.message.ResponseMessage;
import com.blynk.arduino.model.UserProfile;
import com.blynk.arduino.utils.FileManager;
import com.blynk.arduino.utils.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import static com.blynk.arduino.common.enums.Command.SAVE_PROFILE;

/**
 * User: ddumanskiy
 * Date: 11/6/13
 * Time: 8:29 PM
 */
public class SaveProfileHandler extends BaseSimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(SaveProfileHandler.class);

    private static final Command[] ALLOWED_COMMANDS = new Command[] {
            SAVE_PROFILE,
    };

    @Override
    protected Command[] getHandlerCommands() {
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

        String userProfileString = message.getBody();

        //expecting message with 2 parts
        if (userProfileString == null || userProfileString.equals("")) {
            log.error("Save Profile Handler. Income profile message is empty.");
            incomeChannel.write(new ResponseMessage(message, Response.INVALID_COMMAND_FORMAT));
            return;
        }

        log.info("Trying to parseProfile user profile : {}", userProfileString);
        UserProfile userProfile = JsonParser.parseProfile(userProfileString);
        if (userProfile == null) {
            log.error("Register Handler. Wrong user profile message format.");
            incomeChannel.write(new ResponseMessage(message, Response.INVALID_COMMAND_FORMAT));
            return;
        }

        log.info("Trying save user profile.");

        User authUser = Session.getChannelToken().get(incomeChannel.getId());

        authUser.setUserProfile(userProfile);
        boolean profileSaved = FileManager.overrideUserFile(authUser);

        if (profileSaved) {
            incomeChannel.write(new ResponseMessage(message, Response.OK));
        } else {
            incomeChannel.write(new ResponseMessage(message, Response.SERVER_ERROR));
        }
    }

}