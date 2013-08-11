package com.ddumanskiy.arduino.server.handlers;

import com.ddumanskiy.arduino.auth.Session;
import com.ddumanskiy.arduino.server.GroupHolder;
import com.ddumanskiy.arduino.user.User;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import java.util.Iterator;

import static com.ddumanskiy.arduino.common.Consts.LINE_SEPARATOR;
import static com.ddumanskiy.arduino.common.Consts.OK_RESPONSE;

/**
 * User: DOOM
 * Date: 8/11/13
 * Time: 3:38 PM
 */
public class WorkerChannelHandler extends SimpleChannelHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel incomeChannel = e.getChannel();
        String message = (String) e.getMessage();

        System.out.println("Got message from client. Message = " + message);

        //this means not authentificated attempt
        User authUser = Session.getChannelToken().get(incomeChannel.getId());
        if (authUser == null) {
            System.out.println("Channel not authorized. Send login command before commands.");
            incomeChannel.close();
            return;
        }

        DefaultChannelGroup group = GroupHolder.getPrivateRooms().get(authUser);
        Iterator<Channel> iterator = group.iterator();
        while (iterator.hasNext()) {
            Channel current = iterator.next();
            //sending message for all except those one that sends
            if (!current.getId().equals(incomeChannel.getId())) {
                System.out.println("Found channel to send message to " + current.getId() + ", message: " + message);
                current.write(message + LINE_SEPARATOR);
            }
        }

        incomeChannel.write(OK_RESPONSE);
    }

}
