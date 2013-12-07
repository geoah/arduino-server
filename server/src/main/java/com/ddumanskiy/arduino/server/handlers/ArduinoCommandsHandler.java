package com.ddumanskiy.arduino.server.handlers;

import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.common.message.Message;
import com.ddumanskiy.arduino.server.GroupHolder;
import com.ddumanskiy.arduino.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import static com.ddumanskiy.arduino.response.ResponseCode.*;

/**
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 3:38 PM
 */
public class ArduinoCommandsHandler extends SimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(ArduinoCommandsHandler.class);

    private static final String[] ALLOWED_COMMANDS = new String[] {
            "digitalWrite",
            "digitalRead",
            "analogWrite",
            "analogRead"
    };

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        final Channel incomeChannel = e.getChannel();
        final Message message = (Message) e.getMessage();

        String[] messageParts = message.getBody().split(" ");

        if (!isArduinoCommand(messageParts[0])) {
            log.error("Command not supported - {}.", messageParts[0]);
            message.setBody(NOT_SUPPORTED_COMMAND);
            incomeChannel.write(message);
            return;
        }

        //this means not authentificated attempt
        User authUser = Session.getChannelToken().get(incomeChannel.getId());
        if (authUser == null) {
            log.error("Channel not authorized. Send login first.");
            message.setBody(USER_NOT_AUTHENTICATED);
            incomeChannel.write(message);
            return;
        }

        DefaultChannelGroup group = GroupHolder.getPrivateRooms().get(authUser);

        if (group.size() == 1) {
            message.setBody(DEVICE_NOT_IN_NETWORK);
            incomeChannel.write(message);
            return;
        }

        for (Channel current : group) {
            //sending message for all except those one that sends
            if (!current.getId().equals(incomeChannel.getId())) {
                log.info("Found channel to send message to " + current.getId() + ", message: " + message);
                //todo here may be a lot of channels... so how to send response back to user?
                ChannelFuture future = current.write(message);

                future.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) {
                        //todo new message
                        message.setBody(OK);
                        incomeChannel.write(message);
                    }
                });
            }
        }
    }

    private static boolean isArduinoCommand(String commandString) {
        for (String allowedCommand : ALLOWED_COMMANDS) {
            if (commandString.startsWith(allowedCommand)) {
                return true;
            }
        }
        return false;
    }

}
