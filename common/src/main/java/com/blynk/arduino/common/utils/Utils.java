package com.blynk.arduino.common.utils;

import com.blynk.arduino.common.enums.Command;

import java.nio.charset.Charset;

import static com.blynk.arduino.common.enums.Command.*;

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
        Command inputCommandEnum = Command.getByCode(inputCommand);
        for (Command arduinoCommand : ARDUINO_COMMANDS) {
            if (inputCommandEnum == arduinoCommand) {
                return true;
            }
        }
        return false;
    }

    public static boolean isResponseCommand(byte inputCommand) {
        return inputCommand == RESPONSE.getCode();
    }

    private static final Command[] ARDUINO_COMMANDS = new Command[] {
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
