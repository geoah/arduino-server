package com.blynk.arduino.model;

import com.blynk.arduino.utils.JsonParser;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:27
 */
public class JsonParsingTest {

    @Test
    public void testParseUserProfile() {
        InputStream is = this.getClass().getResourceAsStream("/user_profile_json.txt");

        UserProfile userProfile = JsonParser.parseProfile(is);
        assertNotNull(userProfile);
        assertNotNull(userProfile.getDashBoards());
        assertEquals(userProfile.getDashBoards().length, 1);

        DashBoard dashBoard = userProfile.getDashBoards()[0];

        assertNotNull(dashBoard);

        assertEquals(1, dashBoard.getId());
        assertEquals("My Dashboard", dashBoard.getName());
        assertEquals(true, dashBoard.getIsActive());
        assertNotNull(dashBoard.getWidgets());
        assertEquals(dashBoard.getWidgets().length, 8);
        assertNotNull(dashBoard.getSettings());
        assertEquals(dashBoard.getSettings().size(), 2);

        for (Widget widget : dashBoard.getWidgets()) {
            assertNotNull(widget);
            assertEquals(1, widget.getX());
            assertEquals(1, widget.getY());
            assertEquals(1, widget.getDashBoardId());
            assertEquals(1, widget.getId());
            assertEquals("Some Text", widget.getLabel());
        }
    }

    @Test
    public void testUserProfileToJson() {
        InputStream is = this.getClass().getResourceAsStream("/user_profile_json.txt");

        UserProfile userProfile = JsonParser.parseProfile(is);
        String userProfileString = userProfile.toString();

        assertNotNull(userProfileString);
        assertTrue(userProfileString.contains("dashBoards"));
    }

    @Test
    public void testUserProfileToJson2() {
        InputStream is = this.getClass().getResourceAsStream("/user_profile_json_2.txt");

        UserProfile userProfile = JsonParser.parseProfile(is);
        String userProfileString = userProfile.toString();

        assertNotNull(userProfileString);
        assertTrue(userProfileString.contains("dashBoards"));
    }

    @Test
    public void testUserProfileToJson3() {
        InputStream is = this.getClass().getResourceAsStream("/user_profile_json_3.txt");

        UserProfile userProfile = JsonParser.parseProfile(is);
        String userProfileString = userProfile.toString();

        assertNotNull(userProfileString);
        assertTrue(userProfileString.contains("dashBoards"));
    }
}
