package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.EMailValidator;
import com.ddumanskiy.arduino.auth.UserRegistry;
import org.jboss.netty.channel.*;

import static com.ddumanskiy.arduino.common.Consts.BAD_RESPONSE;
import static com.ddumanskiy.arduino.common.Consts.OK_RESPONSE;

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
public class RegisterChannelHandler extends SimpleChannelHandler {

    private static final String REGISTER_TOKEN = "register";

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel incomeChannel = e.getChannel();
        String message = (String) e.getMessage();

        String[] messageParts = message.split(" ");

        //if this is register message (register pupkin@mail.ru hashed_pass)
        if (!isRegisterAction(messageParts[0])) {
            ctx.sendUpstream(e);
            return;
        }

        System.out.println("Register Handler.");
        //expecting message with 3 parts, described above in comment.
        if (messageParts.length != 3) {
            System.out.println("Register Handler. Wrong income message format.");
            incomeChannel.write(BAD_RESPONSE);
            return;
        }

        String user = messageParts[1];
        //TODO encryption, SSL sockets.
        String pass = messageParts[2];
        System.out.println("Register Handler. User : " + user);

        if (!EMailValidator.isValid(user)) {
            System.out.println("Register Handler. Wrong email.");
            incomeChannel.write(BAD_RESPONSE);
            return;
        }

        if (UserRegistry.isUserExists(user)) {
            System.out.println("Register Handler. User with that name already exists.");
            incomeChannel.write(BAD_RESPONSE);
            return;
        }

        System.out.println("Register Handler. Registering.");

        UserRegistry.createNewUser(user, pass);
        incomeChannel.write(OK_RESPONSE);
    }

    private boolean isRegisterAction(String actionName) {
        return REGISTER_TOKEN.equalsIgnoreCase(actionName);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();

        Channel ch = e.getChannel();
               ch.close();
    }
}