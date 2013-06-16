package com.ddumanskiy.arduino.common;

/**
 * User: ddumanskiy
 * Date: 6/16/13
 * Time: 1:58 PM
 */
public abstract class Utils {

    public static Integer getPort(String portString) {
        try {
            return Integer.parseInt(portString);
        } catch (NullPointerException nfe) {
            System.out.println("Port should be an integer.");
        }
        return null;
    }

}
