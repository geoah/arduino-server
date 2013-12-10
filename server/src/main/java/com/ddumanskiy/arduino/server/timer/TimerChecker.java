package com.ddumanskiy.arduino.server.timer;

import com.ddumanskiy.arduino.auth.TimerRegistry;
import com.ddumanskiy.arduino.model.Widget;

import java.util.Iterator;
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

    @Override
    public void run() {
        //todo. here possible problem. while we working with this list,
        //new value may come and override Set<Widgets> and thus this is may be a memory leak
        //because old timers may not be removed,
        //additionally we may get 2 times same value. Chance is small, but exists, so this will be refactored
        // for now ignoring.
        long now = System.currentTimeMillis() / 1000L;

        for (Set<Widget> userTimers : TimerRegistry.getStartTimers().values()) {
            Iterator<Widget> iterator = userTimers.iterator();
            while (iterator.hasNext()) {
                Widget widget = iterator.next();
                if (widget.getStartTime() <= now) {
                    iterator.remove();
                    //todo trigger event
                }
            }
        }

        for (Set<Widget> userTimers : TimerRegistry.getStopTimers().values()) {
            Iterator<Widget> iterator = userTimers.iterator();
            while (iterator.hasNext()) {
                Widget widget = iterator.next();
                if (widget.getStopTime() <= now) {
                    iterator.remove();
                    //todo trigger event
                }
            }
        }
    }

}
