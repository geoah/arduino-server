package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.UserRegistry;
import com.ddumanskiy.arduino.common.Command;
import com.ddumanskiy.arduino.common.message.Message;
import com.ddumanskiy.arduino.mail.EMailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import static com.ddumanskiy.arduino.server.response.ResponseCode.*;

/**
 * Get input message. Checks if it is a register command.
 * If it is, divides input sting by spaces on 3 parts:
 * "message" "username" "password".
 * Checks if user not registered yet. If not - registering.
 *
 * For instance, incoming message may be : "register user@mail.ua my_password"
 *
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 5:41 PM
 */
public class RegisterHandler extends BaseSimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(RegisterHandler.class);

    private static final byte[] ALLOWED_COMMANDS = new byte[] {
            Command.REGISTER,
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

        //if this is register message (register pupkin@mail.ru hashed_pass)
        if (!isHandlerCommand(message.getCommand())) {
            ctx.sendUpstream(e);
            return;
        }

        //expecting message with 3 parts, described above in comment.
        if (messageParts.length != 2) {
            log.error("Register Handler. Wrong income message format.");
            message.setBody(INVALID_COMMAND_FORMAT);
            incomeChannel.write(message);
            return;
        }

        String userName = messageParts[0].toLowerCase();
        //TODO encryption, SSL sockets.
        String pass = messageParts[1];
        log.info("Trying register user : {}", userName);

        if (!EMailValidator.isValid(userName)) {
            log.error("Register Handler. Wrong email: {}", userName);
            message.setBody(INVALID_COMMAND_FORMAT);
            incomeChannel.write(message);
            return;
        }

        if (UserRegistry.isUserExists(userName)) {
            log.error("User with name {} already exists.", userName);
            message.setBody(USER_ALREADY_REGISTERED);
            incomeChannel.write(message);
            return;
        }

        log.info("Registering {}.", userName);

        UserRegistry.createNewUser(userName, pass);

        message.setBody(OK);
        incomeChannel.write(message);
    }

}