package com.ddumanskiy.arduino.common.encoders;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

/**
 * User: ddumanskiy
 * Date: 06.12.13
 * Time: 14:46
 */
public class MessageIdEncoder extends OneToOneEncoder {

    private int messageIdLength;

    public MessageIdEncoder(int messageIdLength) {
        super();
        this.messageIdLength = messageIdLength;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer)) {
            return msg;
        }

        ChannelBuffer body = (ChannelBuffer) msg;
        ChannelBuffer header = channel.getConfig().getBufferFactory().getBuffer(body.order(), messageIdLength);

        int messageid = 1;
        switch (messageIdLength) {
            case 1:
                header.writeByte((byte) messageid);
                break;
            case 2:
                header.writeShort((short) messageid);
                break;
            case 3:
                header.writeMedium(messageid);
                break;
            case 4:
                header.writeInt(messageid);
                break;
            case 8:
                header.writeLong(messageid);
                break;
            default:
                throw new Error("should not reach here");
        }
        return wrappedBuffer(header, body);
    }
}