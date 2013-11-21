package com.ddumanskiy.arduino.model;

import java.util.Arrays;
import java.util.Map;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:04
 */
public class DashBoard {

    private long id;

    private String name;

    private boolean isActive;

    private Widget[] widgets;

    private Map<String, String> settings;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public Widget[] getWidgets() {
        return widgets;
    }

    public void setWidgets(Widget[] widgets) {
        this.widgets = widgets;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashBoard dashBoard = (DashBoard) o;

        if (id != dashBoard.id) return false;
        if (isActive != dashBoard.isActive) return false;
        if (name != null ? !name.equals(dashBoard.name) : dashBoard.name != null) return false;
        if (settings != null ? !settings.equals(dashBoard.settings) : dashBoard.settings != null) return false;
        if (!Arrays.equals(widgets, dashBoard.widgets)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (isActive ? 1 : 0);
        result = 31 * result + (widgets != null ? Arrays.hashCode(widgets) : 0);
        result = 31 * result + (settings != null ? settings.hashCode() : 0);
        return result;
    }
}
