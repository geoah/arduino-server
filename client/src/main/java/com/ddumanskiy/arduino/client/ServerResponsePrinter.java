package com.ddumanskiy.arduino.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger log = LogManager.getLogger(ServerResponsePrinter.class);

    public ServerResponsePrinter() {

    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        //this is already duplicated im MessageIdAndCommand3BytesEncoder
        //Message serverMessage = (Message) e.getMessage();
        //log.info("Server response : {}", serverMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        log.error(e);
        e.getChannel().close();
    }

}
