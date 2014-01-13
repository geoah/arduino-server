package com.ddumanskiy.arduino.graph;

/**
 * Created by ddumanskiy on 1/13/14.
 */
public class ReadValue {

    private long ts;

    private int value;

    public ReadValue(long ts, int value) {
        this.ts = ts;
        this.value = value;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
