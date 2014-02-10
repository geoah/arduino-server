package com.blynk.arduino.server.timer;

import com.blynk.arduino.auth.TimerRegistry;
import com.blynk.arduino.auth.User;
import com.blynk.arduino.model.Widget;
import com.blynk.arduino.model.enums.PinType;
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
    private Widget widget;

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
        Set<Widget> widgets = new HashSet<Widget>() {
            {
                add(widget);
            }
        };

        //when(user.hashCode()).thenReturn(1);
        TimerRegistry.getStartTimers().put(user, widgets);

        when(widget.getStartTime()).thenReturn(System.currentTimeMillis() / 1000 - 10);
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

        when(widget.getPinType()).thenReturn(PinType.DIGITAL);

        when(channel.getAttachment()).thenReturn(ChannelType.ARDUINO);

        timerChecker.run();

        verify(channelPipeline).sendDownstream(any(DownstreamMessageEvent.class));
    }

}
