package com.blynk.arduino.common.enums;

/**
 * User: ddumanskiy
 * Date: 2/10/14
 * Time: 11:58 PM
 */
public enum Command {

    RESPONSE(0),
    
    //mobile client command
    REGISTER(1),
    LOGIN(2),
    SAVE_PROFILE(3),
    LOAD_PROFILE(4),
    GET_TOKEN(5),
    GRAPH_GET(7),
    GRAPH_LOAD(8),


    //arduino commands
    DIGITAL_WRITE(10),
    DIGITAL_READ(11),
    ANALOG_WRITE(20),
    ANALOG_READ(21),

    VIRTUAL_WRITE(30),
    VIRTUAL_READ(31),

    RESET(40),
    RESET_ALL(41),

    SERIAL_WRITE(50),
    SERIAL_READ(51);

    private byte code;

    Command(int code) {
        this.code = (byte) code;
    }

    public byte getCode() {
        return code;
    }

    public static Command getByName(String inCommand) {
        for (Command command : values()) {
            if (command.name().equalsIgnoreCase(inCommand)) {
                return command;
            }
        }

        return null;
    }

    public static Command getByCode(byte code) {
        for (Command command : values()) {
            if (command.getCode() == code) {
                return command;
            }
        }
        return null;
    }
}
