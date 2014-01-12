package com.ddumanskiy.arduino.server.handlers;

import com.ddumanskiy.arduino.auth.User;
import com.ddumanskiy.arduino.auth.UserRegistry;
import com.ddumanskiy.arduino.common.Command;
import com.ddumanskiy.arduino.common.message.Message;
import com.ddumanskiy.arduino.server.handlers.enums.ChannelType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import static com.ddumanskiy.arduino.server.response.ResponseCode.INVALID_TOKEN;

/**
 * Used before login in order to validate pass and brute force basic def.
 *
 * User: ddumanskiy
 * Date: 08.12.13
 * Time: 16:45
 */
public class ArduinoTokenHandler extends BaseSimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(ArduinoTokenHandler.class);

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

        if (messageParts.length != 1) {
            ctx.sendUpstream(e);
            return;
        }


        String arduinoToken = messageParts[0];

        User user = UserRegistry.getByToken(arduinoToken);

        if (user == null) {
            log.error("Arduino token {} doesn't valid.", arduinoToken);
            message.setBody(INVALID_TOKEN);
            incomeChannel.write(message);
            return;
        }

        //simulate correct login message, replace token with user data
        message.setBody(user.getName() + " " + user.getPass());
        incomeChannel.setAttachment(ChannelType.ARDUINO);
        ctx.sendUpstream(e);
    }
}
