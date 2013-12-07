package com.ddumanskiy.arduino.response;

import com.ddumanskiy.arduino.utils.JsonParser;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 9:45 PM
 */
public class ResponseCode {

    public static final String OK = new ResponseCode(1).toString();

    public static final String INVALID_COMMAND_FORMAT = new ResponseCode(2).toString();
    public static final String USER_NOT_REGISTERED = new ResponseCode(3).toString();
    public static final String USER_ALREADY_REGISTERED = new ResponseCode(4).toString();
    public static final String USER_NOT_AUTHENTICATED = new ResponseCode(5).toString();
    public static final String NOT_ALLOWED = new ResponseCode(6).toString();
    public static final String DEVICE_NOT_IN_NETWORK = new ResponseCode(7).toString();

    private int responseCode;

    private ResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
