package com.ddumanskiy.arduino.common.decoders;

import com.ddumanskiy.arduino.common.message.Message;
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
    protected Message decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        Message message = new Message(buffer);
        log.info("Getting : {}", message);
        return message;
    }
}
