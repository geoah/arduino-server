package com.ddumanskiy.arduino.server;

import com.ddumanskiy.arduino.common.Utils;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 5:43 PM
 */
public class Server {

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("You need to specify port.");
            System.out.println("For instance: 'java -jar server.jar 8080'");
            return;
        }

        Integer port = Utils.getPort(args[0]);
        if (port == null) {
            return;
        }

        new Server(port).start();
    }

    public void start() {
        System.out.println("Server started.");

        ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),  Executors.newCachedThreadPool());
        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelsPipe());

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        bootstrap.bind(new InetSocketAddress(port));
     }
}
