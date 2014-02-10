package com.ddumanskiy.arduino.common.enums;

/**
 * User: ddumanskiy
 * Date: 07.12.13
 * Time: 13:39
 */
public enum Response {

    OK((byte) 200),
    INVALID_COMMAND_FORMAT((byte) 2),
    USER_NOT_REGISTERED((byte) 3),
    USER_ALREADY_REGISTERED((byte) 4),
    USER_NOT_AUTHENTICATED((byte) 5),
    NOT_ALLOWED((byte) 6),
    DEVICE_NOT_IN_NETWORK((byte) 7),
    NOT_SUPPORTED_COMMAND((byte) 8),
    INVALID_TOKEN((byte) 9),
    SERVER_ERROR((byte) 10);

    private byte code;

    Response(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static Response getEnumByCode(int code) {
        for (Response response : values()) {
            if (response.code == code) {
                return response;
            }
        }
        return null;
    }
}
