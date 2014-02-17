package com.blynk.arduino.server.handlers;

import com.blynk.arduino.auth.User;
import com.blynk.arduino.auth.UserRegistry;
import com.blynk.arduino.common.enums.Command;
import com.blynk.arduino.common.enums.Response;
import com.blynk.arduino.common.message.MobileClientMessage;
import com.blynk.arduino.common.message.ResponseMessage;
import com.blynk.arduino.server.handlers.enums.ChannelType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import static com.blynk.arduino.common.enums.Command.LOGIN;

/**
 * Used before login in order to validate pass and brute force basic def.
 *
 * User: ddumanskiy
 * Date: 08.12.13
 * Time: 16:45
 */
public class ArduinoTokenHandler extends BaseSimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(ArduinoTokenHandler.class);

    private static final Command[] ALLOWED_COMMANDS = new Command[] {
            LOGIN,
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

        String[] messageParts = message.getBody().split(" ", 2);

        if (messageParts.length != 1) {
            ctx.sendUpstream(e);
            return;
        }


        String arduinoToken = messageParts[0];

        User user = UserRegistry.getByToken(arduinoToken);

        if (user == null) {
            log.error("Arduino token {} doesn't valid.", arduinoToken);
            incomeChannel.write(new ResponseMessage(message, Response.INVALID_TOKEN));
            return;
        }

        //simulate correct login message, replace token with user data
        message.setBody(user.getName() + " " + user.getPass());
        incomeChannel.setAttachment(ChannelType.ARDUINO);
        ctx.sendUpstream(e);
    }

}
