package com.ddumanskiy.arduino.common.message;

import org.jboss.netty.buffer.ChannelBuffer;

import java.nio.charset.Charset;

/**
 * User: ddumanskiy
 * Date: 07.12.13
 * Time: 13:39
 */
public class Message {

    private short messageId;

    private String body;

    private static final Charset defaultCharset = Charset.defaultCharset();

    public Message(short messageId, String body) {
        this.messageId = messageId;
        this.body = body;
    }

    public Message(ChannelBuffer cb) {
        //todo here depending on messageID field length should be different method invocation
        messageId = cb.readShort();
        body = cb.toString(defaultCharset);

        //this is just for netty. moving read index to the end
        cb.readerIndex(cb.capacity());
    }

    public short getMessageId() {
        return messageId;
    }

    public void setMessageId(short messageId) {
        this.messageId = messageId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", body='" + body + '\'' +
                '}';
    }
}
