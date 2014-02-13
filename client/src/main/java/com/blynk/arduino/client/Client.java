package com.blynk.arduino.client;

import com.blynk.arduino.common.decoders.MessageIdAndCommand3BytesDecoder;
import com.blynk.arduino.common.encoders.MessageIdAndCommand3BytesEncoder;
import com.blynk.arduino.common.enums.Command;
import com.blynk.arduino.common.message.ArduinoMessage;
import com.blynk.arduino.common.message.MobileClientMessage;
import com.blynk.arduino.common.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.Executors;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 8:09 PM
 */
public class Client {

    private static final Logger log = LogManager.getLogger(Client.class);

    private int port;
    private String host;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("You need to specify server host, port and auth token.");
            System.out.println("For instance: 'java -jar client.jar localhost 8080'");
            return;
        }

        String host = args[0];
        Integer port = Utils.getPort(args[1]);
        if (port == null) {
            return;
        }

        new Client(port, host).start();
    }

    public void start() {
        ChannelFactory factory =
                new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool());

        ClientBootstrap bootstrap = new ClientBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(
                        //downstream
                        new LengthFieldPrepender(2),
                        new MessageIdAndCommand3BytesEncoder(),

                        //upstream
                        new LengthFieldBasedFrameDecoder(Short.MAX_VALUE, 0, 2, 0, 2),
                        new MessageIdAndCommand3BytesDecoder(),
                        new ServerResponsePrinter()
                );
            }
        });

        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);

        ChannelFuture f;
        try {
            // Start the client.
            f = bootstrap.connect(new InetSocketAddress(host, port)).sync();

            readUserInput(f.getChannel());

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private final Random random = new Random();

    private void readUserInput(Channel serverChannel) throws Exception {
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

        String line;
        while ((line = consoleInput.readLine()) != null) {
            String[] input = line.split(" ");

            byte command;

            try {
                Command commandEnum = Command.getByName(input[0]);
                if (commandEnum == null) {
                    log.error("Unknown command.");
                    continue;
                }
                command = commandEnum.getCode();
            } catch (IllegalArgumentException e) {
                log.error(e);
                continue;
            }

            if (Utils.isArduinoCommand(command)) {
                Byte pin = input.length > 1 ? Byte.valueOf(input[1]) : null;
                Short value = input.length > 2 ? Short.valueOf(input[2]) : null;
                serverChannel.write(new ArduinoMessage((short)random.nextInt(Short.MAX_VALUE), command, pin, value));
            } else {
                input = line.split(" ", 2);
                String body = input.length == 1 ? "" : input[1];
                serverChannel.write(new MobileClientMessage((short)random.nextInt(Short.MAX_VALUE), command, body));
            }
        }
    }

}
