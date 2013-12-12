package com.ddumanskiy.arduino.server.timer;

import com.ddumanskiy.arduino.auth.TimerRegistry;
import com.ddumanskiy.arduino.auth.User;
import com.ddumanskiy.arduino.common.Command;
import com.ddumanskiy.arduino.common.message.Message;
import com.ddumanskiy.arduino.model.Widget;
import com.ddumanskiy.arduino.model.enums.PinType;
import com.ddumanskiy.arduino.server.GroupHolder;
import com.ddumanskiy.arduino.server.handlers.ArduinoCommandsHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Used to check timer widgets among all users.
 * Check performed every second (may be changed in future).
 *
 * User: ddumanskiy
 * Date: 09.12.13
 * Time: 7:05
 */
public class TimerChecker implements Runnable {

    private long sleepIntervalMillis;
    private ChannelPipeline channelPipeline;

    private static final Logger log = LogManager.getLogger(TimerChecker.class);

    public TimerChecker(long sleepIntervalMillis, ChannelPipeline channelPipeline) {
        this.sleepIntervalMillis = sleepIntervalMillis;
        this.channelPipeline = channelPipeline;
    }

    @Override
    public void run() {
        //todo. here possible problem. while we working with this list,
        //new value may come and override Set<Widgets> and thus this is may be a memory leak
        //because old timers may not be removed,
        //additionally we may get 2 times same value. Chance is small, but exists, so this will be refactored
        // for now ignoring.
        long now = System.currentTimeMillis() / 1000L;

        for (Map.Entry<User, Set<Widget>> userTimers : TimerRegistry.getStartTimers().entrySet()) {
            Iterator<Widget> iterator = userTimers.getValue().iterator();
            while (iterator.hasNext()) {
                Widget widget = iterator.next();
                if (widget.getStartTime() <= now) {
                    iterator.remove();
                    //todo fix value
                    log.info("Sending timer message. User : {},  widget : {}", userTimers.getKey(), widget);
                    channelPipeline.sendDownstream(createMessage(userTimers.getKey(), widget, "1"));
                }
            }
        }

        for (Map.Entry<User, Set<Widget>> userTimers : TimerRegistry.getStopTimers().entrySet()) {
            Iterator<Widget> iterator = userTimers.getValue().iterator();
            while (iterator.hasNext()) {
                Widget widget = iterator.next();
                if (widget.getStopTime() <= now) {
                    iterator.remove();
                    //todo fix value
                    log.info("Sending timer message. User : {},  widget : {}", userTimers.getKey(), widget);
                    DownstreamMessageEvent message = createMessage(userTimers.getKey(), widget, "0");
                    channelPipeline.sendDownstream(message);
                }
            }
        }

        try {
            Thread.sleep(sleepIntervalMillis);
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

    private static DownstreamMessageEvent createMessage(User user, Widget timer, String value) {
        DefaultChannelGroup group = GroupHolder.getPrivateRooms().get(user);

        if (group == null) {
            //todo define what to do here
            log.error("Timer failed. No user and arduino board. {}, {}", user, timer);
            return null;
        }

        //find arduino channels
        for (Channel outChannel : group) {
            if (ArduinoCommandsHandler.isArduinoChannel(outChannel)) {
                byte command;
                if (timer.getPinType() == PinType.ANALOG) {
                    command = Command.ANALOG_WRITE;
                } else if (timer.getPinType() == PinType.DIGITAL) {
                    command = Command.DIGITAL_WRITE;
                } else {
                    command = Command.VIRTUAL_WRITE;
                }

                Message message = new Message((short)0, command, String.valueOf(timer.getPin()) + value);
                return new DownstreamMessageEvent(outChannel, Channels.future(outChannel), message, null);
            }
        }

        return null;
    }

}
