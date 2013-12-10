package com.ddumanskiy.arduino.model;

import com.ddumanskiy.arduino.utils.JsonParser;

import java.util.Arrays;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:04
 */
public class UserProfile {

    private DashBoard[] dashBoards;

    public DashBoard[] getDashBoards() {
        return dashBoards;
    }

    public void setDashBoards(DashBoard[] dashBoards) {
        this.dashBoards = dashBoards;
    }

    public DashBoard getActiveDashboard() {
        if (dashBoards == null || dashBoards.length == 0) {
            return null;
        }

        for (DashBoard dashBoard : dashBoards) {
            if (dashBoard.getIsActive()) {
                return dashBoard;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProfile that = (UserProfile) o;

        if (!Arrays.equals(dashBoards, that.dashBoards)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return dashBoards != null ? Arrays.hashCode(dashBoards) : 0;
    }
}
