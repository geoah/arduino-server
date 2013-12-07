package com.ddumanskiy.arduino.common.encoders;

import com.ddumanskiy.arduino.common.Utils;
import com.ddumanskiy.arduino.common.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * User: ddumanskiy
 * Date: 06.12.13
 * Time: 14:46
 */
public class MessageId2BytesEncoder extends OneToOneEncoder {

    private static final Logger log = LogManager.getLogger(MessageId2BytesEncoder.class);

    private static final int MESSAGE_ID_FIELD_LENGTH_BYTES = 2;

    public MessageId2BytesEncoder() {
        super();
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof Message)) {
            return msg;
        }

        log.info("Sending : {}", msg);

        ChannelBuffer messageIdBuffer = ChannelBuffers.buffer(MESSAGE_ID_FIELD_LENGTH_BYTES);
        messageIdBuffer.writeShort(((Message) msg).getMessageId());

        return ChannelBuffers.wrappedBuffer(
                messageIdBuffer,
                ChannelBuffers.copiedBuffer(((Message) msg).getBody(), Utils.DEFAULT_CHARSET)
        );
    }
}