package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.EMailValidator;
import com.ddumanskiy.arduino.auth.UserRegistry;
import com.ddumanskiy.arduino.mail.MailTLS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.*;

import java.util.UUID;

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

    private static final Logger log = LogManager.getLogger(RegisterChannelHandler.class);

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

        //expecting message with 3 parts, described above in comment.
        if (messageParts.length != 3) {
            log.error("Register Handler. Wrong income message format.");
            incomeChannel.write(BAD_RESPONSE);
            return;
        }

        String user = messageParts[1].toLowerCase();
        //TODO encryption, SSL sockets.
        String pass = messageParts[2];
        log.info("Trying register user : {}", user);

        if (!EMailValidator.isValid(user)) {
            log.error("Register Handler. Wrong email: {}", user);
            incomeChannel.write(BAD_RESPONSE);
            return;
        }

        if (UserRegistry.isUserExists(user)) {
            log.error("User with name {} already exists.", user);
            incomeChannel.write(BAD_RESPONSE);
            return;
        }

        log.info("Registering {}.", user);

        String id = UUID.randomUUID().toString();

        UserRegistry.createNewUser(user, pass, id);
        MailTLS.sendMail(user, "You just registered to Arduino control.", id);
        incomeChannel.write(OK_RESPONSE);
    }

    private boolean isRegisterAction(String actionName) {
        return REGISTER_TOKEN.equalsIgnoreCase(actionName);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        log.error("Error in {}", this.getClass().getName());
        log.error(e.getCause().fillInStackTrace());

        Channel ch = e.getChannel();
        ch.close();
    }
}