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
public class MessageIdAndCommand3BytesEncoder extends OneToOneEncoder {

    private static final Logger log = LogManager.getLogger(MessageIdAndCommand3BytesEncoder.class);

    private static final int MESSAGE_ID_FIELD_LENGTH_BYTES = 2;
    private static final int COMMAND_FIELD_LENGTH_BYTES = 1;

    public MessageIdAndCommand3BytesEncoder() {
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

        ChannelBuffer commandBuffer = ChannelBuffers.buffer(COMMAND_FIELD_LENGTH_BYTES);
        commandBuffer.writeByte(((Message) msg).getCommand());

        return ChannelBuffers.wrappedBuffer(
                messageIdBuffer,
                commandBuffer,
                ChannelBuffers.copiedBuffer(((Message) msg).getBody(), Utils.DEFAULT_CHARSET)
        );
    }
}