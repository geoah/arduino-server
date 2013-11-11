package com.ddumanskiy.arduino.server.handlers;

import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.server.GroupHolder;
import com.ddumanskiy.arduino.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import java.util.Iterator;

import static com.ddumanskiy.arduino.common.Consts.OK_RESPONSE;

/**
 * User: DOOM
 * Date: 8/11/13
 * Time: 3:38 PM
 */
public class WorkerChannelHandler extends SimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(WorkerChannelHandler.class);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel incomeChannel = e.getChannel();
        String message = (String) e.getMessage();

        //this means not authentificated attempt
        User authUser = Session.getChannelToken().get(incomeChannel.getId());
        if (authUser == null) {
            log.error("Channel not authorized. Send login first. Closing socket.");
            incomeChannel.close();
            return;
        }

        DefaultChannelGroup group = GroupHolder.getPrivateRooms().get(authUser);
        Iterator<Channel> iterator = group.iterator();
        while (iterator.hasNext()) {
            Channel current = iterator.next();
            //sending message for all except those one that sends
            if (!current.getId().equals(incomeChannel.getId())) {
                log.info("Found channel to send message to " + current.getId() + ", message: " + message);
                current.write(message);
            }
        }

        incomeChannel.write(OK_RESPONSE);
    }

}
