package com.blynk.arduino.server.handlers;


import com.blynk.arduino.auth.Session;
import com.blynk.arduino.auth.User;
import com.blynk.arduino.auth.UserRegistry;
import com.blynk.arduino.common.enums.Command;
import com.blynk.arduino.common.enums.Response;
import com.blynk.arduino.common.message.MobileClientMessage;
import com.blynk.arduino.common.message.ResponseMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import static com.blynk.arduino.common.enums.Command.GET_TOKEN;

/**
 * User: ddumanskiy
 * Date: 11/6/13
 * Time: 8:29 PM
 */
public class GetTokenHandler extends BaseSimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(GetTokenHandler.class);

    private static final Command[] ALLOWED_COMMANDS = new Command[] {
            GET_TOKEN,
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

        String dashBoardIdString = message.getBody();

        Long dashBoardId;
        try {
            dashBoardId = Long.parseLong(dashBoardIdString);
        } catch (NumberFormatException ex) {
            log.error("Dash board id {} not valid.", dashBoardIdString);
            incomeChannel.write(new ResponseMessage(message, Response.INVALID_COMMAND_FORMAT));
            return;
        }

        User user = Session.getChannelToken().get(incomeChannel.getId());
        String token = UserRegistry.getToken(user, dashBoardId);

        message.setBody(token);
        incomeChannel.write(message);
    }

}