package com.ddumanskiy.arduino.client;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * User: ddumanskiy
 * Date: 6/16/13
 * Time: 3:20 PM
 */
public class ConsoleMessagesSender implements Runnable {

    private Channel serverChannel;
    private BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

    public ConsoleMessagesSender(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    @Override
    public void run() {
       String line;

        try {
            while ((line = consoleInput.readLine()) != null) {
                serverChannel.write(
                        ChannelBuffers.copiedBuffer((line + System.getProperty("line.separator")).getBytes())
                );
            }
        } catch (IOException e) {
            System.out.println("Error getting console input.");
        } finally {
            serverChannel.close();
        }

    }
}
