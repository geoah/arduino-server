package com.ddumanskiy.arduino.client;

import com.ddumanskiy.arduino.common.Command;
import com.ddumanskiy.arduino.common.Utils;
import com.ddumanskiy.arduino.common.decoders.MessageIdAndCommand3BytesDecoder;
import com.ddumanskiy.arduino.common.encoders.MessageIdAndCommand3BytesEncoder;
import com.ddumanskiy.arduino.common.message.Message;
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
            String[] input = line.split(" ", 2);

            byte command;

            try {
                command = getCommand(input[0]);
            } catch (IllegalArgumentException e) {
                log.error(e);
                continue;
            }

            serverChannel.write(new Message((short)random.nextInt(Short.MAX_VALUE), command, input[1]));
        }
    }

    private static byte getCommand(String commandWord) {
        if ("register".equalsIgnoreCase(commandWord)) {
            return Command.REGISTER;
        }
        if ("login".equalsIgnoreCase(commandWord)) {
            return Command.LOGIN;
        }
        if ("loadProfile".equalsIgnoreCase(commandWord)) {
            return Command.LOAD_PROFILE;
        }
        if ("saveProfile".equalsIgnoreCase(commandWord)) {
            return Command.SAVE_PROFILE;
        }
        if ("digitalWrite".equalsIgnoreCase(commandWord)) {
            return Command.DIGITAL_WRITE;
        }
        if ("digitalRead".equalsIgnoreCase(commandWord)) {
            return Command.DIGITAL_READ;
        }
        if ("analogWrite".equalsIgnoreCase(commandWord)) {
            return Command.ANALOG_WRITE;
        }
        if ("analogRead".equalsIgnoreCase(commandWord)) {
            return Command.ANALOG_READ;
        }
        if ("virtualWrite".equalsIgnoreCase(commandWord)) {
            return Command.VIRTUAL_WRITE;
        }
        if ("virtualRead".equalsIgnoreCase(commandWord)) {
            return Command.VIRTUAL_READ;
        }
        if ("reset".equalsIgnoreCase(commandWord)) {
            return Command.RESET;
        }
        if ("resetAll".equalsIgnoreCase(commandWord)) {
            return Command.RESET_ALL;
        }

        throw new IllegalArgumentException("Wrong command " + commandWord);
    }

}
