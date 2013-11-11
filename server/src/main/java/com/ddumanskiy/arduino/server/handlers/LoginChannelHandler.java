package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.auth.UserRegistry;
import com.ddumanskiy.arduino.server.GroupHolder;
import com.ddumanskiy.arduino.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static com.ddumanskiy.arduino.common.Consts.BAD_RESPONSE;
import static com.ddumanskiy.arduino.common.Consts.OK_RESPONSE;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 5:41 PM
 */
public class LoginChannelHandler extends SimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(LoginChannelHandler.class);

    private final Map<String, Integer> brutterDefence = new HashMap<String, Integer>();

    private static final String LOGIN_TOKEN = "login";

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel incomeChannel = e.getChannel();
        String message = (String) e.getMessage();

        String[] messageParts = message.split(" ");

        if (!isLoginToken(messageParts[0])) {
            ctx.sendUpstream(e);
            return;
        }

        if (messageParts.length != 3) {
            log.error("Wrong income message format.");
            incomeChannel.write(BAD_RESPONSE);
            return;
        }


        String userName = messageParts[1];
        //TODO encryption, SSL sockets.
        String pass = messageParts[2];
        log.info("User : {}", userName);

        if (!UserRegistry.isUserExists(userName)) {
            log.error("User {} not registered.", userName);
            incomeChannel.write(BAD_RESPONSE);
            return;
        }

        User user = UserRegistry.getByName(userName);

        //key == userName + host;
        String key = userName + ((InetSocketAddress) incomeChannel.getRemoteAddress()).getHostName();

        if (brutterDefence.get(key) != null && brutterDefence.get(key) > 5) {
            log.error("Too many tries for login from 1 host. Blocking until server restart.");
            incomeChannel.write(BAD_RESPONSE);
            return;
        }

        //todo fix pass validation
        if (!user.getPass().equals(pass)) {
            log.error("Bad password. Please try again.");
            incomeChannel.write(BAD_RESPONSE);

            Integer tries = brutterDefence.get(key);
            brutterDefence.put(key, tries == null ? 1 : ++tries);

            return;
        }

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

        incomeChannel.write(OK_RESPONSE);
    }

    private boolean isLoginToken(String actionName) {
        return LOGIN_TOKEN.equalsIgnoreCase(actionName);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        log.error("Error in {}", this.getClass().getName());
        log.error(e.getCause());

        Channel ch = e.getChannel();
        ch.close();
    }
}