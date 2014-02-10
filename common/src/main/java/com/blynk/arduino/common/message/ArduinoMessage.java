package com.blynk.arduino.common.message;

/**
 * User: ddumanskiy
 * Date: 07.12.13
 * Time: 13:39
 */
public class ArduinoMessage extends Message {

    private Byte pin;

    private Short value;

    public ArduinoMessage(short messageId, byte command, Byte pin, Short value) {
        super(messageId, command);
        this.pin = pin;
        this.value = value;
    }

    public Byte getPin() {
        return pin;
    }

    public void setPin(Byte pin) {
        this.pin = pin;
    }

    public Short getValue() {
        return value;
    }

    public void setValue(Short value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ArduinoMessage{" +
                "pin=" + pin +
                ", value=" + value +
                ", " + super.toString() +
                '}';
    }
}
