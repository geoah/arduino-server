package com.ddumanskiy.arduino.server.handlers;

import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.server.GroupHolder;
import com.ddumanskiy.arduino.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import static com.ddumanskiy.arduino.response.ResponseCode.DEVICE_NOT_IN_NETWORK;
import static com.ddumanskiy.arduino.response.ResponseCode.OK;

/**
 * User: DOOM
 * Date: 8/11/13
 * Time: 3:38 PM
 */
public class WorkerChannelHandler extends SimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(WorkerChannelHandler.class);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        final Channel incomeChannel = e.getChannel();
        String message = (String) e.getMessage();

        //this means not authentificated attempt
        User authUser = Session.getChannelToken().get(incomeChannel.getId());
        if (authUser == null) {
            log.error("Channel not authorized. Send login first. Closing socket.");
            incomeChannel.close();
            return;
        }

        DefaultChannelGroup group = GroupHolder.getPrivateRooms().get(authUser);

        if (group.size() == 1) {
            incomeChannel.write(DEVICE_NOT_IN_NETWORK);
            return;
        }

        for (Channel current : group) {
            //sending message for all except those one that sends
            if (!current.getId().equals(incomeChannel.getId())) {
                log.info("Found channel to send message to " + current.getId() + ", message: " + message);
                //todo here may be a lot of channels... so how to send response back to user?
                ChannelFuture future = current.write(message);

                future.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) {
                        incomeChannel.write(OK);
                    }
                });
            }
        }
    }

}
