package com.ddumanskiy.arduino.common;

/**
 * User: ddumanskiy
 * Date: 08.12.13
 * Time: 11:05
 */
public class Command {

    public static final byte REGISTER = 1;
    public static final byte LOGIN = 2;
    public static final byte SAVE_PROFILE = 3;
    public static final byte LOAD_PROFILE = 4;

    public static final byte DIGITAL_WRITE = 10;
    public static final byte DIGITAL_READ = 11;

    public static final byte ANALOG_WRITE = 20;
    public static final byte ANALOG_READ = 21;

    public static final byte VIRTUAL_WRITE = 30;
    public static final byte VIRTUAL_READ = 31;

    public static final byte RESET = 40;
    public static final byte RESET_ALL = 41;

}
