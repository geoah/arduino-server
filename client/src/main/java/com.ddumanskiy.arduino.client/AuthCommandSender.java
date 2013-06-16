package com.ddumanskiy.arduino.client;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 8:14 PM
 */
public class AuthCommandSender extends SimpleChannelHandler {

    private String authToken;

    public AuthCommandSender(String authToken) {
        if (authToken == null) {
            throw new NullPointerException("Can't send null authToken.");
        }
        this.authToken = authToken + System.getProperty("line.separator");
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Channel serverChannel = e.getChannel();
        ChannelBuffer messageBuffer = ChannelBuffers.copiedBuffer(authToken.getBytes());
        serverChannel.write(messageBuffer);

        new Thread(new ConsoleMessagesSender(serverChannel)).start();
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
