package com.ddumanskiy.arduino.common.message;

/**
 * User: ddumanskiy
 * Date: 07.12.13
 * Time: 13:39
 */
public class Message {

    private short messageId;

    private byte command;

    private String body;

    public Message() {
    }

    public Message(short messageId, byte command, String body) {
        this.messageId = messageId;
        this.command = command;
        this.body = body;
    }

    public short getMessageId() {
        return messageId;
    }

    public void setMessageId(short messageId) {
        this.messageId = messageId;
    }

    public byte getCommand() {
        return command;
    }

    public void setCommand(byte command) {
        this.command = command;
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
                ", command=" + command +
                ", body='" + body + '\'' +
                '}';
    }
}
