package com.ddumanskiy.arduino.client;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 8:14 PM
 */
public class ServerResponsePrinter extends SimpleChannelHandler {

    public ServerResponsePrinter() {

    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ChannelBuffer serverChannel = (ChannelBuffer) e.getMessage();

        byte[] readed = new byte[serverChannel.capacity()];
        serverChannel.getBytes(0, readed);

        System.out.println(new String(readed));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }

}
