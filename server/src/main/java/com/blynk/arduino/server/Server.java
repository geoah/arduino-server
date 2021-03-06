package com.blynk.arduino.server;

import com.blynk.arduino.common.utils.Utils;
import com.blynk.arduino.server.timer.TimerChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 5:43 PM
 */
public class Server {

    private static final Logger log = LogManager.getLogger(Server.class);

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            log.error("You need to specify port.");
            log.error("For instance: 'java -jar server.jar 8080'");
            return;
        }

        Integer port = Utils.getPort(args[0]);
        if (port == null) {
            return;
        }

        new Server(port).start();
    }

    public void start() {
        ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),  Executors.newCachedThreadPool());
        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        ChannelsPipe channelsPipe = new ChannelsPipe();
        bootstrap.setPipelineFactory(channelsPipe);

        //bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        bootstrap.bind(new InetSocketAddress(port));

        //starting timer processing thread

        ScheduledExecutorService scheduledTimerThreadPool = Executors.newScheduledThreadPool(1);


        try {
            scheduledTimerThreadPool.scheduleAtFixedRate(new TimerChecker(channelsPipe.getPipeline()), 0, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e);
        }

        log.info("Server started.");
     }
}
