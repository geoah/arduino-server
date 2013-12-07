package com.ddumanskiy.arduino.common.decoders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * Used to fetch messageId from message and put it in context.
 *
 * User: ddumanskiy
 * Date: 06.12.13
 * Time: 14:01
 */
public class MessageIdDecoder extends FrameDecoder {

    private static final Logger log = LogManager.getLogger(MessageIdDecoder.class);

    private int messageIdFieldLength;

    public MessageIdDecoder(int messageIdFieldLength) {
        super();

        if (messageIdFieldLength <= 0) {
            throw new IllegalArgumentException(
                    "messageIdFieldLength must be a positive integer: " +
                            messageIdFieldLength);
        }

        this.messageIdFieldLength = messageIdFieldLength;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        ChannelBuffer messageId = buffer.copy(0, messageIdFieldLength);
        ctx.setAttachment(messageId);
        //todo here depending on messageID field length should be defferent method invocation
        //log.info("Message id : {}", messageId.getShort(0));

        int actualFrameLength = buffer.readableBytes() - messageIdFieldLength;
        ChannelBuffer messageBody = buffer.copy(messageIdFieldLength, actualFrameLength);
        buffer.readerIndex(buffer.readableBytes());
        return messageBody;
    }
}
