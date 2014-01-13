package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.auth.User;
import com.ddumanskiy.arduino.common.Command;
import com.ddumanskiy.arduino.common.message.Message;
import com.ddumanskiy.arduino.graph.GraphDataStorage;
import com.ddumanskiy.arduino.utils.JsonParser;
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
public class GraphCommandsHandler extends BaseSimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(GraphCommandsHandler.class);

    private static final byte[] ALLOWED_COMMANDS = new byte[] {
            Command.GRAPH_GET,
            Command.GRAPH_LOAD
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
            log.error("Wrong command format {}.", message.getBody());
            message.setBody(INVALID_COMMAND_FORMAT);
            incomeChannel.write(message);
            return;
        }

        String stringPin = messageParts[0];
        String stringTS = messageParts[1];

        User authUser = Session.getChannelToken().get(incomeChannel.getId());

        if (message.getCommand() == Command.GRAPH_GET) {
            Long ts = null;
            try {
                //todo not used for now, finish.
                ts = Long.parseLong(stringTS);
            } catch (NumberFormatException ex) {
                log.error("Timestamp {} not valid.", stringTS);
                message.setBody(INVALID_COMMAND_FORMAT);
                incomeChannel.write(message);
                return;
            }

            Object[] data = GraphDataStorage.getAllData(authUser.getName(), stringPin);
            message.setBody(JsonParser.toJson(data));
            incomeChannel.write(message);
        }
    }

}