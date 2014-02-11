package com.blynk.arduino.model;

import com.blynk.arduino.model.enums.PinType;
import com.blynk.arduino.model.enums.State;
import com.blynk.arduino.model.enums.WidgetType;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:08
 */
public class Widget {

    private long id;

    private int x;

    private int y;

    private Integer width;

    private Integer height;

    private long dashBoardId;

    private String label;

    private WidgetType type;

    private PinType pinType;

    private Integer pin;

    private String value;

    private State state;

    private Long readingFrequency;

    //for TIMER widget
    //unix time
    private Long startTime;
    private Long stopTime;
    private Integer interval;

    //for SLIDER
    private Boolean pwm;
    private Integer min;
    private Integer max;


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

    public PinType getPinType() {
        return pinType;
    }

    public void setPinType(PinType pinType) {
        this.pinType = pinType;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
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

    public Long getReadingFrequency() {
        return readingFrequency;
    }

    public void setReadingFrequency(Long readingFrequency) {
        this.readingFrequency = readingFrequency;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Boolean getPwm() {
        return pwm;
    }

    public void setPwm(Boolean pwm) {
        this.pwm = pwm;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
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
        if (pinType != widget.pinType) return false;
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
        result = 31 * result + (pinType != null ? pinType.hashCode() : 0);
        result = 31 * result + (pin != null ? pin.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (stopTime != null ? stopTime.hashCode() : 0);
        return result;
    }
}
