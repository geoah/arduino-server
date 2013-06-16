package com.ddumanskiy.arduino.server.handlers;


import com.ddumanskiy.arduino.auth.Authentification;
import com.ddumanskiy.arduino.server.GroupHolder;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import java.util.Iterator;

import static com.ddumanskiy.arduino.common.Consts.LINE_SEPARATOR;
import static com.ddumanskiy.arduino.common.Consts.OK_RESPONSE;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 5:41 PM
 */
public class AuthChannelHandler extends SimpleChannelHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel incomeChannel = e.getChannel();
        String message = (String) e.getMessage();

        System.out.println("Got message from client. Message = " + message);

        if (Authentification.isAuthToken(message)) {
            if (Authentification.isRegisteredUser(message)) {
                //here we need to be sure that same user don't want ot connect second time
                if (Authentification.getChannelToken().get(incomeChannel.getId()) == null) {
                    Authentification.getChannelToken().put(incomeChannel.getId(), message);

                    DefaultChannelGroup group = GroupHolder.getPrivateRooms().get(message);
                    //only one side came
                    if (group == null) {
                        System.out.println("Creating unique group for token: " + message);
                        group = new DefaultChannelGroup(message);
                        GroupHolder.getPrivateRooms().put(message, group);
                    }

                    group.add(incomeChannel);
                    System.out.println("Adding channel with id " + incomeChannel.getId() + " and token " + message + " to group.");

                }
            }
        } else {
            //this is for all other messsages that are not auth

            //this means not authentificated attempt
            String authToken = Authentification.getChannelToken().get(incomeChannel.getId());
            if (authToken == null) {
                System.out.println("Channel not authorized. Send AUTH command before commands.");
                incomeChannel.close();
            }

            DefaultChannelGroup group = GroupHolder.getPrivateRooms().get(authToken);
            Iterator<Channel> iterator = group.iterator();
            while (iterator.hasNext()) {
                Channel current = iterator.next();
                //sending message for all except those one that sends
                if (!current.getId().equals(incomeChannel.getId())) {
                    System.out.println("Found channel to send message to " + current.getId() + ", message: " + message);
                    current.write(message + LINE_SEPARATOR);
                }
            }

        }



        incomeChannel.write(OK_RESPONSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();

        Channel ch = e.getChannel();
               ch.close();
    }
}