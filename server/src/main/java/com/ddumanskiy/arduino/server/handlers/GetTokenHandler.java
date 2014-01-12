package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.auth.User;
import com.ddumanskiy.arduino.auth.UserRegistry;
import com.ddumanskiy.arduino.common.Command;
import com.ddumanskiy.arduino.common.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import static com.ddumanskiy.arduino.server.response.ResponseCode.INVALID_COMMAND_FORMAT;

/**
 * User: ddumanskiy
 * Date: 11/6/13
 * Time: 8:29 PM
 */
public class GetTokenHandler extends BaseSimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(GetTokenHandler.class);

    private static final byte[] ALLOWED_COMMANDS = new byte[] {
            Command.GET_TOKEN,
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

        String dashBoardIdString = message.getBody();

        Long dashBoardId;
        try {
            dashBoardId = Long.parseLong(dashBoardIdString);
        } catch (NumberFormatException ex) {
            log.error("Dash board id {} not valid.", dashBoardIdString);
            message.setBody(INVALID_COMMAND_FORMAT);
            incomeChannel.write(message);
            return;
        }

        User user = Session.getChannelToken().get(incomeChannel.getId());
        String token = UserRegistry.getToken(user, dashBoardId);

        message.setBody(token);
        incomeChannel.write(message);
    }

}