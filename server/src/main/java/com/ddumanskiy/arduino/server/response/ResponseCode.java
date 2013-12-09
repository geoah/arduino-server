package com.ddumanskiy.arduino.server.response;

import com.ddumanskiy.arduino.utils.JsonParser;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 9:45 PM
 */
public class ResponseCode {

    public static final String OK = new ResponseCode(200).toString();

    public static final String INVALID_COMMAND_FORMAT = new ResponseCode(2).toString();
    public static final String USER_NOT_REGISTERED = new ResponseCode(3).toString();
    public static final String USER_ALREADY_REGISTERED = new ResponseCode(4).toString();
    public static final String USER_NOT_AUTHENTICATED = new ResponseCode(5).toString();
    public static final String NOT_ALLOWED = new ResponseCode(6).toString();
    public static final String DEVICE_NOT_IN_NETWORK = new ResponseCode(7).toString();
    public static final String NOT_SUPPORTED_COMMAND = new ResponseCode(8).toString();
    public static final String SERVER_ERROR = new ResponseCode(500).toString();

    private int code;

    private ResponseCode(int responseCode) {
        this.code = responseCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}

