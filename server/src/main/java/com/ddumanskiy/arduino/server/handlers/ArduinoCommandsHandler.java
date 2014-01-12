package com.ddumanskiy.arduino.server.handlers;

import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.auth.User;
import com.ddumanskiy.arduino.common.Command;
import com.ddumanskiy.arduino.common.message.Message;
import com.ddumanskiy.arduino.server.GroupHolder;
import com.ddumanskiy.arduino.server.handlers.enums.ChannelType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import static com.ddumanskiy.arduino.server.response.ResponseCode.*;

/**
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 3:38 PM
 */
public class ArduinoCommandsHandler extends BaseSimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(ArduinoCommandsHandler.class);

    private static final byte[] ALLOWED_COMMANDS = new byte[] {
            Command.DIGITAL_WRITE,
            Command.DIGITAL_READ,
            Command.ANALOG_READ,
            Command.ANALOG_WRITE,
            Command.VIRTUAL_READ,
            Command.VIRTUAL_WRITE,
            Command.RESET,
            Command.RESET_ALL
    };

    @Override
    protected byte[] getHandlerCommands() {
        return ALLOWED_COMMANDS;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        final Channel incomeChannel = e.getChannel();
        final Message message = (Message) e.getMessage();

        if (!isHandlerCommand(message.getCommand())) {
            log.error("Command not supported - {}.", message.getCommand());
            message.setBody(NOT_SUPPORTED_COMMAND);
            incomeChannel.write(message);
            return;
        }

        //this means not authentificated attempt
        User authUser = Session.getChannelToken().get(incomeChannel.getId());

        DefaultChannelGroup group = GroupHolder.getPrivateRooms().get(authUser);

        if (group.size() == 1) {
            message.setBody(DEVICE_NOT_IN_NETWORK);
            incomeChannel.write(message);
            return;
        }

        for (Channel outChannel : group) {
            //sending message for all except those one that sends
            if (!outChannel.getId().equals(incomeChannel.getId()) && isCorrectChannel(outChannel, incomeChannel)) {
                log.info("Found channel to send message to " + outChannel.getId() + ", message: " + message);
                //todo here may be a lot of channels... so how to send response back to user?
                ChannelFuture future = outChannel.write(message);

                future.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) {
                        //todo new message
                        message.setBody(OK);
                        incomeChannel.write(message);
                    }
                });
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        log.error(e.getCause());

    }

    private static boolean isCorrectChannel(Channel outChannel, Channel incomeChannel) {
        //if message from mobile client, than out channel should be arduino
        if (incomeChannel.getAttachment() == null || incomeChannel.getAttachment() == ChannelType.MOBILE_CLIENT) {
            return isArduinoChannel(outChannel);
        //if income channel arduino, than output should be mobile client
        } else {
            return isMobileChannel(outChannel);
        }

    }

    public static boolean isArduinoChannel(Channel channel) {
        return channel.getAttachment() != null && (channel.getAttachment() == ChannelType.ARDUINO);
    }

    public static boolean isMobileChannel(Channel channel) {
        return channel.getAttachment() == null || (channel.getAttachment() == ChannelType.MOBILE_CLIENT);
    }
}
