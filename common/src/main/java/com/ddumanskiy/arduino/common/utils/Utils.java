package com.ddumanskiy.arduino.common.utils;

import com.ddumanskiy.arduino.common.Command;

import java.nio.charset.Charset;

import static com.ddumanskiy.arduino.common.Command.*;

/**
 * User: ddumanskiy
 * Date: 6/16/13
 * Time: 1:58 PM
 */
public abstract class Utils {

    public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    public static Integer getPort(String portString) {
        try {
            return Integer.parseInt(portString);
        } catch (NullPointerException nfe) {
            System.out.println("Port should be an integer.");
        }
        return null;
    }

    public static boolean isArduinoCommand(byte inputCommand) {
        for (byte arduinoCommand : ARDUINO_COMMANDS) {
            if (inputCommand == arduinoCommand) {
                return true;
            }
        }
        return false;
    }

    public static boolean isResponseCommand(byte inputCommand) {
        return inputCommand == Command.RESPONSE;
    }

    private static final byte[] ARDUINO_COMMANDS = new byte[] {
            DIGITAL_WRITE,
            DIGITAL_READ,
            ANALOG_READ,
            ANALOG_WRITE,
            VIRTUAL_READ,
            VIRTUAL_WRITE,
            RESET,
            RESET_ALL
    };

}
