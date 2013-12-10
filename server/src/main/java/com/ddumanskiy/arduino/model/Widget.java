package com.ddumanskiy.arduino.model;

import com.ddumanskiy.arduino.model.enums.State;
import com.ddumanskiy.arduino.model.enums.WidgetType;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:08
 */
public class Widget {

    private long id;

    private int x;

    private int y;

    private long dashBoardId;

    private String label;

    private WidgetType type;

    private String pin;

    private String value;

    private State state;

    //for TIMER widget
    //unix time
    private Long startTime;
    private Long stopTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public long getDashBoardId() {
        return dashBoardId;
    }

    public void setDashBoardId(long dashBoardId) {
        this.dashBoardId = dashBoardId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public WidgetType getType() {
        return type;
    }

    public void setType(WidgetType type) {
        this.type = type;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getStopTime() {
        return stopTime;
    }

    public void setStopTime(Long stopTime) {
        this.stopTime = stopTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Widget widget = (Widget) o;

        if (dashBoardId != widget.dashBoardId) return false;
        if (id != widget.id) return false;
        if (x != widget.x) return false;
        if (y != widget.y) return false;
        if (label != null ? !label.equals(widget.label) : widget.label != null) return false;
        if (pin != null ? !pin.equals(widget.pin) : widget.pin != null) return false;
        if (startTime != null ? !startTime.equals(widget.startTime) : widget.startTime != null) return false;
        if (state != widget.state) return false;
        if (stopTime != null ? !stopTime.equals(widget.stopTime) : widget.stopTime != null) return false;
        if (type != widget.type) return false;
        if (value != null ? !value.equals(widget.value) : widget.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + (int) (dashBoardId ^ (dashBoardId >>> 32));
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (pin != null ? pin.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (stopTime != null ? stopTime.hashCode() : 0);
        return result;
    }
}
