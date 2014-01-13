package com.ddumanskiy.arduino.graph;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds user data that arduino sends over a time.
 * Key is username + ":" + pin
 * Value is Queue of retrieved values
 *
 * Created by ddumanskiy on 1/13/14.
 */
public class GraphDataStorage {

    private static Map<String, CircularFifoBuffer> data = new ConcurrentHashMap<>();

    private static final Object[] EMPTY_RESPONSE = new Object[0];

    public static void add(String username, String pin, int value) {
        String key = makeKey(username, pin);
        CircularFifoBuffer fifoBuffer = data.get(key);
        if (fifoBuffer == null) {
            fifoBuffer = new CircularFifoBuffer(1000);
            data.put(key, fifoBuffer);
        }

        long ts = System.currentTimeMillis();
        fifoBuffer.add(new ReadValue(ts, value));
    }

    //todo concurrency issues.
    public static Object[] getAllData(String username, String pin) {
        String key = makeKey(username, pin);
        CircularFifoBuffer fifoBuffer = data.get(key);
        if (fifoBuffer != null) {
            Object[] result = fifoBuffer.toArray();
            fifoBuffer.clear();
            return result;
        }

        return EMPTY_RESPONSE;
    }


    private static String makeKey(String username, String pin) {
        return username + ":" + pin;
    }


}
