package com.ddumanskiy.arduino.auth;

import com.ddumanskiy.arduino.model.DashBoard;
import com.ddumanskiy.arduino.model.UserProfile;
import com.ddumanskiy.arduino.model.Widget;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: ddumanskiy
 * Date: 09.12.13
 * Time: 9:25
 */
public class TimerRegistry {

    private static final ConcurrentHashMap<String, Set<Widget>> startTimers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Set<Widget>> stopTimers = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, Set<Widget>> getStartTimers() {
        return startTimers;
    }

    public static ConcurrentHashMap<String, Set<Widget>> getStopTimers() {
        return stopTimers;
    }

    /**
     * Checks if user has timer widgets. If has, adds them to map. For future processing.
     * @param user - user to check timers at.
     */
    public static void checkUserHasTimers(User user) {
        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            startTimers.remove(user.getName());
            stopTimers.remove(user.getName());
            return;
        }

        DashBoard activeDashboard = profile.getActiveDashboard();

        if (activeDashboard == null) {
            startTimers.remove(user.getName());
            stopTimers.remove(user.getName());
            return;
        }

        //finally adding widgets to registry, replacing existing

        Set<Widget> stopTimeWidgets = validateStopTimers(activeDashboard.getTimerWidgets());
        if (stopTimeWidgets == null || stopTimeWidgets.size() == 0) {
            stopTimers.remove(user.getName());
        } else {
            stopTimers.put(user.getName(), stopTimeWidgets);
        }

        Set<Widget> startTimeWidgets = validateStartTimers(activeDashboard.getTimerWidgets());
        if (startTimeWidgets == null || startTimeWidgets.size() == 0) {
            startTimers.remove(user.getName());
        } else {
            startTimers.put(user.getName(), startTimeWidgets);
        }

    }

    private static Set<Widget> validateStartTimers(Set<Widget> widgets) {
        if (widgets == null || widgets.size() == 0) {
            return null;
        }

        Set<Widget> validatedWidgets = new HashSet<>();
        for (Widget timer : widgets) {
            if (isStartTimerValid(timer)) {
                validatedWidgets.add(timer);
            }
        }

        return validatedWidgets;
    }

    private static boolean isStartTimerValid(Widget timer) {
        //unix now
        long now = System.currentTimeMillis() / 1000L;

        if (timer.getStartTime() != null && timer.getStartTime() > now) {
            return true;
        }

        return false;
    }

    private static Set<Widget> validateStopTimers(Set<Widget> widgets) {
        if (widgets == null || widgets.size() == 0) {
            return null;
        }

        Set<Widget> validatedWidgets = new HashSet<>();
        for (Widget timer : widgets) {
            if (isStopTimerValid(timer)) {
                validatedWidgets.add(timer);
            }
        }

        return validatedWidgets;
    }

    private static boolean isStopTimerValid(Widget timer) {
        //unix now
        long now = System.currentTimeMillis() / 1000L;

        if (timer.getStopTime() != null && timer.getStopTime() > now) {
            return true;
        }

        return false;
    }
}
