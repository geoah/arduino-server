package com.blynk.arduino.common.message;

import com.blynk.arduino.common.enums.Response;

import static com.blynk.arduino.common.enums.Command.RESPONSE;

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
        super(msg.getMessageId(), RESPONSE.getCode());
        this.code = code.getCode();
    }

    public ResponseMessage(short messageId, byte code) {
        super(messageId, RESPONSE.getCode());
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
