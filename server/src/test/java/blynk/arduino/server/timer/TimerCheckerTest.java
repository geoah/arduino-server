package com.blynk.arduino.server.timer;

import com.blynk.arduino.auth.User;
import com.blynk.arduino.auth.UserRegistry;
import com.blynk.arduino.model.DashBoard;
import com.blynk.arduino.model.UserProfile;
import com.blynk.arduino.model.Widget;
import com.blynk.arduino.model.enums.PinType;
import com.blynk.arduino.model.enums.WidgetType;
import com.blynk.arduino.server.GroupHolder;
import com.blynk.arduino.server.handlers.enums.ChannelType;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Iterator;

import static org.mockito.Mockito.*;

/**
 * User: ddumanskiy
 * Date: 10.12.13
 * Time: 11:30
 */
@RunWith(MockitoJUnitRunner.class)
public class TimerCheckerTest {

    @Mock
    private ChannelPipeline channelPipeline;

    @Mock
    private Widget timerWidget;

    @Mock
    private User user;

    @Mock
    private Channel channel;

    @Mock
    private DefaultChannelGroup group;

    @InjectMocks
    private TimerChecker timerChecker;


    @Test
    public void testTimerChecker() {
        User user = UserRegistry.createNewUser("test", "test");
        UserProfile profile = new UserProfile();
        user.setUserProfile(profile);

        DashBoard dashBoard = new DashBoard();
        dashBoard.setIsActive(true);
        dashBoard.setWidgets(new Widget[] {timerWidget});
        profile.setDashBoards(new DashBoard[] {dashBoard});


        when(timerWidget.getType()).thenReturn(WidgetType.TIMER);
        when(timerWidget.getStartTime()).thenReturn(System.currentTimeMillis());
        when(timerWidget.getPinType()).thenReturn(PinType.DIGITAL);
        when(timerWidget.getStopInterval()).thenReturn(60000);

        GroupHolder.getPrivateRooms().put(user, group);
        when(group.iterator()).thenReturn(new Iterator<org.jboss.netty.channel.Channel>() {
            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Channel next() {
                hasNext = false;
                return channel;
            }

            @Override
            public void remove() {

            }
        });


        when(channel.getAttachment()).thenReturn(ChannelType.ARDUINO);

        timerChecker.run();

        verify(channelPipeline, times(1)).sendDownstream(any(DownstreamMessageEvent.class));
    }

}
