package com.ddumanskiy.arduino.common.decoders;

import com.ddumanskiy.arduino.common.message.ArduinoMessage;
import com.ddumanskiy.arduino.common.message.Message;
import com.ddumanskiy.arduino.common.message.MobileClientMessage;
import com.ddumanskiy.arduino.common.message.ResponseMessage;
import com.ddumanskiy.arduino.common.utils.Utils;
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
public class MessageIdAndCommand3BytesDecoder extends FrameDecoder {

    private static final Logger log = LogManager.getLogger(MessageIdAndCommand3BytesDecoder.class);

    public MessageIdAndCommand3BytesDecoder() {
        super();
    }

    @Override
    protected Message decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        short messageId = buffer.readShort();
        byte command = buffer.readByte();

        Message message;

        if (Utils.isResponseCommand(command)) {
            message = new ResponseMessage(messageId, buffer.readByte());
        } else if (Utils.isArduinoCommand(command)) {
            Byte pin = null;
            Short value = null;
            if (buffer.readableBytes() > 0) {
                pin = buffer.readByte();
                if (buffer.readableBytes() > 0) {
                    value = buffer.readShort();
                }
            }
            message = new ArduinoMessage(messageId, command, pin, value);
        } else {
            message = new MobileClientMessage(messageId, command, buffer.toString(Utils.DEFAULT_CHARSET));
        }

        //this is just for netty. moving read index to the end
        buffer.readerIndex(buffer.capacity());

        log.info("Getting : {}", message);
        return message;
    }
}
