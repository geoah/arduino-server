package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.auth.User;
import com.ddumanskiy.arduino.auth.UserRegistry;
import com.ddumanskiy.arduino.common.Command;
import com.ddumanskiy.arduino.common.message.Message;
import com.ddumanskiy.arduino.server.GroupHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import static com.ddumanskiy.arduino.server.response.ResponseCode.OK;
import static com.ddumanskiy.arduino.server.response.ResponseCode.USER_NOT_REGISTERED;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 5:41 PM
 */
public class LoginHandler extends BaseSimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(LoginHandler.class);

    private static final byte[] ALLOWED_COMMANDS = new byte[] {
            Command.LOGIN,
    };

    @Override
    protected byte[] getHandlerCommands() {
        return ALLOWED_COMMANDS;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel incomeChannel = e.getChannel();
        Message message = (Message) e.getMessage();

        String[] messageParts = message.getBody().split(" ", 2);

        if (!isHandlerCommand(message.getCommand())) {
            ctx.sendUpstream(e);
            return;
        }

        String userName = messageParts[0].toLowerCase();
        log.info("User : {}", userName);

        if (!UserRegistry.isUserExists(userName)) {
            log.error("User {} not registered.", userName);
            message.setBody(USER_NOT_REGISTERED);
            incomeChannel.write(message);
            return;
        }

        User user = UserRegistry.getByName(userName);

        //here we need to be sure that same user don't want ot connect second time
        if (Session.getChannelToken().get(incomeChannel.getId()) == null) {
            Session.getChannelToken().put(incomeChannel.getId(), user);

            DefaultChannelGroup group = GroupHolder.getPrivateRooms().get(user);
            //only one side came
            if (group == null) {
                log.info("Creating unique group for user: {}", user);
                group = new DefaultChannelGroup(user.getName());
                GroupHolder.getPrivateRooms().put(user, group);
            }

            group.add(incomeChannel);
            log.info("Adding channel with id {} to userGroup {}.", incomeChannel.getId(), user.getName());
        }

        message.setBody(OK);
        incomeChannel.write(message);
    }

}