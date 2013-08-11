package com.ddumanskiy.arduino.client;

import com.ddumanskiy.arduino.common.Consts;
import com.ddumanskiy.arduino.common.Utils;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LineBasedFrameDecoder;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 8:09 PM
 */
public class Client {

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
                        new LineBasedFrameDecoder(Consts.MAX_AUTH_STRING_LENGTH),
                        new AuthCommandSender()
                );
            }
        });

        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);

        bootstrap.connect(new InetSocketAddress(host, port));
    }

}
