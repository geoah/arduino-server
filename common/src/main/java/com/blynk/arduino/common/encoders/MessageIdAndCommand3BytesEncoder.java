package com.blynk.arduino.common.encoders;

import com.blynk.arduino.common.message.ArduinoMessage;
import com.blynk.arduino.common.message.Message;
import com.blynk.arduino.common.message.MobileClientMessage;
import com.blynk.arduino.common.message.ResponseMessage;
import com.blynk.arduino.common.utils.Utils;
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

        if (msg instanceof ArduinoMessage) {
            return build((ArduinoMessage) msg);
        } else if (msg instanceof MobileClientMessage) {
            return build((MobileClientMessage) msg);
        } else {
            return build((ResponseMessage) msg);
        }
    }

    private static ChannelBuffer build(ResponseMessage message) {
        ChannelBuffer commonBuffer = buildCommonMessagePart(message);

        ChannelBuffer codeBuffer = ChannelBuffers.buffer(1);
        codeBuffer.writeByte(message.getCode());

        return ChannelBuffers.wrappedBuffer(
                commonBuffer,
                codeBuffer
        );
    }

    private static ChannelBuffer build(MobileClientMessage message) {
        ChannelBuffer commonBuffer = buildCommonMessagePart(message);

        return ChannelBuffers.wrappedBuffer(
                commonBuffer,
                ChannelBuffers.copiedBuffer(message.getBody(), Utils.DEFAULT_CHARSET)
        );
    }

    private static ChannelBuffer build(ArduinoMessage message) {
        ChannelBuffer commonBuffer = buildCommonMessagePart(message);

        ChannelBuffer pinBuffer = null;
        ChannelBuffer valueBuffer = null;

        if (message.getPin() != null) {
            pinBuffer = ChannelBuffers.buffer(1);
            pinBuffer.writeByte(message.getPin());

            if (message.getValue() != null) {
                valueBuffer = ChannelBuffers.buffer(2);
                valueBuffer.writeShort(message.getValue());
            }
        }

        return ChannelBuffers.wrappedBuffer(
                commonBuffer,
                pinBuffer,
                valueBuffer
        );
    }

    private static ChannelBuffer buildCommonMessagePart(Message msg) {
        ChannelBuffer commonBuffer = ChannelBuffers.buffer(MESSAGE_ID_FIELD_LENGTH_BYTES + COMMAND_FIELD_LENGTH_BYTES);
        commonBuffer.writeShort(msg.getMessageId());
        commonBuffer.writeByte(msg.getCommand());
        return commonBuffer;
    }
}