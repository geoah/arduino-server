package com.ddumanskiy.arduino.common.message;

/**
 * User: ddumanskiy
 * Date: 07.12.13
 * Time: 13:39
 */
public class MobileClientMessage extends Message {

    private String body;

    public MobileClientMessage(short messageId, byte command, String body) {
        super(messageId, command);
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "MobileClientMessage{" +
                "body='" + body + '\'' +
                ", " + super.toString() +
                '}';
    }
}
