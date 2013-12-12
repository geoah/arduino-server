package com.ddumanskiy.arduino.auth;

import com.ddumanskiy.arduino.model.DashBoard;
import com.ddumanskiy.arduino.model.UserProfile;
import com.ddumanskiy.arduino.model.Widget;
import com.ddumanskiy.arduino.model.enums.PinType;
import com.ddumanskiy.arduino.model.enums.WidgetType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: ddumanskiy
 * Date: 10.12.13
 * Time: 10:38
 */
public class TimerRegistryTest {

    private User user;

    @Before
    public void cleanup() {
        TimerRegistry.getStopTimers().clear();
        TimerRegistry.getStartTimers().clear();

        long time = System.currentTimeMillis() / 1000 + 60;

        Widget widget = new Widget();
        widget.setPin(1);
        widget.setPinType(PinType.DIGITAL);
        widget.setType(WidgetType.TIMER);
        widget.setStartTime(time);
        widget.setStopTime(time);

        DashBoard dashBoard = new DashBoard();
        dashBoard.setWidgets(new Widget[] {widget});
        dashBoard.setIsActive(true);

        UserProfile userProfile = new UserProfile();
        userProfile.setDashBoards(new DashBoard[] {dashBoard});

        user = new User();
        user.setName("test");
        user.setUserProfile(userProfile);
    }

    @Test
    public void testBaseScenario() {
        TimerRegistry.checkUserHasTimers(user);

        assertEquals(1, TimerRegistry.getStartTimers().size());
        assertEquals(1, TimerRegistry.getStopTimers().size());
    }

    @Test
    public void testDateInPastScenario() {
        long pastTime = System.currentTimeMillis() / 1000;
        user.getUserProfile().getActiveDashboard().getWidgets()[0].setStartTime(pastTime);
        user.getUserProfile().getActiveDashboard().getWidgets()[0].setStopTime(pastTime);
        TimerRegistry.checkUserHasTimers(user);

        assertEquals(0, TimerRegistry.getStartTimers().size());
        assertEquals(0, TimerRegistry.getStopTimers().size());
    }

    @Test
    public void testDateInPastScenario2() {
        long pastTime = System.currentTimeMillis() / 1000;
        user.getUserProfile().getActiveDashboard().getWidgets()[0].setStartTime(pastTime);

        pastTime += 60;
        user.getUserProfile().getActiveDashboard().getWidgets()[0].setStopTime(pastTime);
        TimerRegistry.checkUserHasTimers(user);

        assertEquals(0, TimerRegistry.getStartTimers().size());
        assertEquals(1, TimerRegistry.getStopTimers().size());
    }

}
