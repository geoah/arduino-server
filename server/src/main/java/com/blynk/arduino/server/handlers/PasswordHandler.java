package com.blynk.arduino.server.handlers;

import com.blynk.arduino.auth.User;
import com.blynk.arduino.auth.UserRegistry;
import com.blynk.arduino.common.Command;
import com.blynk.arduino.common.enums.Response;
import com.blynk.arduino.common.message.MobileClientMessage;
import com.blynk.arduino.common.message.ResponseMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

        if (!isHandlerCommand(e.getMessage())) {
            ctx.sendUpstream(e);
            return;
        }

        MobileClientMessage message = (MobileClientMessage) e.getMessage();

        String[] messageParts = message.getBody().split(" ", 2);

        if (messageParts.length != 2) {
            log.error("Wrong income message format.");
            incomeChannel.write(new ResponseMessage(message, Response.INVALID_COMMAND_FORMAT));
            return;
        }

        String userName = messageParts[0].toLowerCase();
        //TODO encryption, SSL sockets.
        String pass = messageParts[1];

        String host = ((InetSocketAddress) incomeChannel.getRemoteAddress()).getHostName();
        String key = userName + host;

        if (brutterDefence.get(key) != null && brutterDefence.get(key) > 5) {
            log.error("Too many tries for login from 1 host. Blocking. User : {}; host : {}", userName, host);
            incomeChannel.write(new ResponseMessage(message, Response.NOT_ALLOWED));
            return;
        }

        User user = UserRegistry.getByName(userName);

        if (user == null) {
            log.error("User not registered.", userName);
            incomeChannel.write(new ResponseMessage(message, Response.USER_NOT_REGISTERED));
            return;
        }

        //todo fix pass validation
        if (!user.getPass().equals(pass)) {
            log.error("Bad password. User : {}; Pass : {}", userName, pass);
            incomeChannel.write(new ResponseMessage(message, Response.USER_NOT_AUTHENTICATED));

            Integer tries = brutterDefence.get(key);
            brutterDefence.put(key, tries == null ? 1 : ++tries);
            return;
        }

        ctx.sendUpstream(e);
    }
}
