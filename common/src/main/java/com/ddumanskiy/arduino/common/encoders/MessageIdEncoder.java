package com.ddumanskiy.arduino.common.encoders;

import com.ddumanskiy.arduino.common.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import java.nio.charset.Charset;

/**
 * User: ddumanskiy
 * Date: 06.12.13
 * Time: 14:46
 */
public class MessageIdEncoder extends OneToOneEncoder {

    private static final Logger log = LogManager.getLogger(MessageIdEncoder.class);

    private int messageIdLength;

    private static final Charset defaultCharset = Charset.defaultCharset();

    public MessageIdEncoder(int messageIdLength) {
        super();
        this.messageIdLength = messageIdLength;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof Message)) {
            return msg;
        }

        log.info("Sending : {}", msg);

        ChannelBuffer messageIdBuffer = ChannelBuffers.buffer(messageIdLength);
        messageIdBuffer.writeShort(((Message) msg).getMessageId());

        return ChannelBuffers.wrappedBuffer(
                messageIdBuffer,
                ChannelBuffers.copiedBuffer(((Message) msg).getBody(), defaultCharset)
        );
    }
}