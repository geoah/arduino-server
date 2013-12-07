package com.ddumanskiy.arduino.common.decoders;

import com.ddumanskiy.arduino.common.Utils;
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
public class MessageId2BytesDecoder extends FrameDecoder {

    private static final Logger log = LogManager.getLogger(MessageId2BytesDecoder.class);

    public MessageId2BytesDecoder() {
        super();
    }

    @Override
    protected Message decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        Message message = new Message();
        message.setMessageId(buffer.readShort());
        message.setBody(buffer.toString(Utils.DEFAULT_CHARSET));

        //this is just for netty. moving read index to the end
        buffer.readerIndex(buffer.capacity());

        log.info("Getting : {}", message);
        return message;
    }
}
