package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.auth.User;
import com.ddumanskiy.arduino.auth.UserRegistry;
import com.ddumanskiy.arduino.common.Command;
import com.ddumanskiy.arduino.common.enums.Response;
import com.ddumanskiy.arduino.common.message.Message;
import com.ddumanskiy.arduino.common.message.MobileClientMessage;
import com.ddumanskiy.arduino.common.message.ResponseMessage;
import com.ddumanskiy.arduino.server.GroupHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import static com.ddumanskiy.arduino.utils.ChannelsUtils.isArduinoChannel;

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

        if (!isHandlerCommand(e.getMessage())) {
            User authUser = Session.getChannelToken().get(incomeChannel.getId());
            if (authUser == null) {
                log.error("Channel not authorized. Send login first.");
                incomeChannel.write(new ResponseMessage((Message) e.getMessage(), Response.USER_NOT_AUTHENTICATED));
                return;
            }

            ctx.sendUpstream(e);
            return;
        }

        MobileClientMessage message = (MobileClientMessage) e.getMessage();
        String[] messageParts = message.getBody().split(" ", 2);


        String userName = messageParts[0].toLowerCase();
        log.info("User {} trying to login. Client type : {}", userName, isArduinoChannel(incomeChannel) ? "Arduino" : "Mobile");

        if (!UserRegistry.isUserExists(userName)) {
            log.error("User {} not registered.", userName);
            incomeChannel.write(new ResponseMessage(message, Response.USER_NOT_REGISTERED));
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

        incomeChannel.write(new ResponseMessage(message, Response.OK));
    }

}