package com.ddumanskiy.arduino.server.handlers;

import com.ddumanskiy.arduino.auth.User;
import com.ddumanskiy.arduino.auth.UserRegistry;
import com.ddumanskiy.arduino.common.Command;
import com.ddumanskiy.arduino.common.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ddumanskiy.arduino.server.response.ResponseCode.*;

/**
 * Used before login in order to validate pass and brute force basic def.
 *
 * User: ddumanskiy
 * Date: 08.12.13
 * Time: 16:45
 */
public class PasswordHandler extends BaseSimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(PasswordHandler.class);

    private static final Map<String, Integer> brutterDefence = new ConcurrentHashMap<>();

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

        if (!isHandlerCommand(message.getCommand())) {
            ctx.sendUpstream(e);
            return;
        }

        String[] messageParts = message.getBody().split(" ", 2);

        if (messageParts.length != 2) {
            log.error("Wrong income message format.");
            message.setBody(INVALID_COMMAND_FORMAT);
            incomeChannel.write(message);
            return;
        }

        String userName = messageParts[0].toLowerCase();
        //TODO encryption, SSL sockets.
        String pass = messageParts[1];

        String host = ((InetSocketAddress) incomeChannel.getRemoteAddress()).getHostName();
        String key = userName + host;

        if (brutterDefence.get(key) != null && brutterDefence.get(key) > 5) {
            log.error("Too many tries for login from 1 host. Blocking. User : {}; host : {}", userName, host);
            message.setBody(NOT_ALLOWED);
            incomeChannel.write(message);
            return;
        }

        User user = UserRegistry.getByName(userName);

        if (user == null) {
            log.error("User not registered.", userName);
            message.setBody(NOT_ALLOWED);
            incomeChannel.write(message);
            return;
        }

        //todo fix pass validation
        if (!user.getPass().equals(pass)) {
            log.error("Bad password. User : {}; Pass : {}", userName, pass);
            message.setBody(USER_NOT_AUTHENTICATED);
            incomeChannel.write(message);

            Integer tries = brutterDefence.get(key);
            brutterDefence.put(key, tries == null ? 1 : ++tries);
            return;
        }

        ctx.sendUpstream(e);
    }
}
