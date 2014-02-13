package com.blynk.arduino.server.timer;

import com.blynk.arduino.auth.User;
import com.blynk.arduino.auth.UserRegistry;
import com.blynk.arduino.common.message.ArduinoMessage;
import com.blynk.arduino.model.DashBoard;
import com.blynk.arduino.model.UserProfile;
import com.blynk.arduino.model.Widget;
import com.blynk.arduino.model.enums.PinType;
import com.blynk.arduino.server.GroupHolder;
import com.blynk.arduino.utils.ChannelsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import java.util.Collection;
import java.util.Collections;

import static com.blynk.arduino.common.enums.Command.*;

/**
 * Used to check timer widgets among all users.
 * Check performed every second (may be changed in future).
 *
 * User: ddumanskiy
 * Date: 09.12.13
 * Time: 7:05
 */
public class TimerChecker implements Runnable {

    private ChannelPipeline channelPipeline;

    private static final Logger log = LogManager.getLogger(TimerChecker.class);

    public TimerChecker(ChannelPipeline channelPipeline) {
        this.channelPipeline = channelPipeline;
    }

    @Override
    public void run() {
        DateTime now = new DateTime().withMillisOfSecond(0);

        for (User user : UserRegistry.getUsers().values()) {
            Collection<Widget> userTimers = findUserTimers(user);

            for (Widget timer : userTimers) {
                DateTime startTime = new DateTime(timer.getStartTime());

                //start action
                if (equalHourMinuteAndSecond(startTime, now)) {
                    log.info("Preparing for start timer message. User : {},  widgetStart : {}", user, timer);
                    DownstreamMessageEvent event = createMessage(user, timer, (short) 1);
                    channelPipeline.sendDownstream(event);
                }

                startTime = startTime.plusMillis(timer.getStopInterval());
                //stop action
                if (equalHourMinuteAndSecond(startTime, now)) {
                    log.info("Preparing for stop timer message. User : {},  widget : {}", user, timer);
                    DownstreamMessageEvent event = createMessage(user, timer, (short) 0);
                    channelPipeline.sendDownstream(event);
                }
            }
        }
    }

    private static boolean equalHourMinuteAndSecond(DateTime start, DateTime now) {
        return start.get(DateTimeFieldType.hourOfDay()) == now.get(DateTimeFieldType.hourOfDay()) &&
               start.get(DateTimeFieldType.minuteOfDay()) == now.get(DateTimeFieldType.minuteOfDay()) &&
               start.get(DateTimeFieldType.secondOfDay()) == now.get(DateTimeFieldType.secondOfDay());
    }

    private static Collection<Widget> findUserTimers(User user) {
        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            return Collections.emptySet();
        }

        DashBoard activeDashboard = profile.getActiveDashboard();

        if (activeDashboard == null) {
            return Collections.emptySet();
        }

        return activeDashboard.getTimerWidgets();
    }

    private static DownstreamMessageEvent createMessage(User user, Widget timer, short value) {
        DefaultChannelGroup group = GroupHolder.getPrivateRooms().get(user);

        if (group == null) {
            log.error("Timer failed. No user and arduino board.");
            return null;
        }

        //find arduino channels
        for (Channel outChannel : group) {
            if (ChannelsUtils.isArduinoChannel(outChannel)) {
                byte command;
                if (timer.getPinType() == PinType.ANALOG) {
                    command = ANALOG_WRITE.getCode();
                } else if (timer.getPinType() == PinType.VIRTUAL) {
                    command = VIRTUAL_WRITE.getCode();
                } else {
                    command = DIGITAL_WRITE.getCode();
                }

                ArduinoMessage message = new ArduinoMessage((short)0, command, timer.getPin(), value);
                return new DownstreamMessageEvent(outChannel, Channels.future(outChannel), message, null);
            }
        }

        log.error("No arduino in network. Skipping.");
        return null;
    }

}
