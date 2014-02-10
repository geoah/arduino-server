package com.ddumanskiy.arduino.server.handlers;

import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.auth.User;
import com.ddumanskiy.arduino.common.enums.Response;
import com.ddumanskiy.arduino.common.message.Message;
import com.ddumanskiy.arduino.common.message.ResponseMessage;
import com.ddumanskiy.arduino.server.GroupHolder;
import com.ddumanskiy.arduino.server.handlers.enums.ChannelType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import static com.ddumanskiy.arduino.common.Command.*;
import static com.ddumanskiy.arduino.utils.ChannelsUtils.isArduinoChannel;
import static com.ddumanskiy.arduino.utils.ChannelsUtils.isMobileChannel;

/**
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 3:38 PM
 */
public class ArduinoCommandsHandler extends BaseSimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(ArduinoCommandsHandler.class);

    private static final byte[] ALLOWED_COMMANDS = new byte[] {
            DIGITAL_WRITE,
            DIGITAL_READ,
            ANALOG_READ,
            ANALOG_WRITE,
            VIRTUAL_READ,
            VIRTUAL_WRITE,
            RESET,
            RESET_ALL
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
            incomeChannel.write(new ResponseMessage(message, Response.NOT_SUPPORTED_COMMAND));
            return;
        }

        //this means not authentificated attempt
        User authUser = Session.getChannelToken().get(incomeChannel.getId());

        DefaultChannelGroup group = GroupHolder.getPrivateRooms().get(authUser);

        if (group.size() == 1) {
            incomeChannel.write(new ResponseMessage(message, Response.DEVICE_NOT_IN_NETWORK));
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
                        incomeChannel.write(new ResponseMessage(message, Response.OK));
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


}
