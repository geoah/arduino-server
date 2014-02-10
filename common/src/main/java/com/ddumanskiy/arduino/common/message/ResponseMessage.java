package com.ddumanskiy.arduino.common.message;

import com.ddumanskiy.arduino.common.Command;
import com.ddumanskiy.arduino.common.enums.Response;

/**
 * User: ddumanskiy
 * Date: 07.12.13
 * Time: 13:39
 */
public class ResponseMessage extends Message {

    private byte code;

    public byte getCode() {
        return code;
    }

    public ResponseMessage(Message msg, Response code) {
        super(msg.getMessageId(), Command.RESPONSE);
        this.code = code.getCode();
    }

    public ResponseMessage(short messageId, byte code) {
        super(messageId, Command.RESPONSE);
        this.code = code;
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "code=" + Response.getEnumByCode(code) +
                ", " + super.toString() +
                '}';
    }
}
