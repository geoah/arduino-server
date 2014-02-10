package com.blynk.arduino.common.message;

import com.blynk.arduino.common.enums.Command;

/**
 * User: ddumanskiy
 * Date: 07.12.13
 * Time: 13:39
 */
public abstract class Message {

    private short messageId;

    private byte command;

    protected Message(short messageId, byte command) {
        this.messageId = messageId;
        this.command = command;
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

    @Override
    public String toString() {
        return "messageId=" + messageId +
                ", command=" + Command.getByCode(command);
    }
}
