package com.ddumanskiy.arduino.server;

import com.ddumanskiy.arduino.server.handlers.LoginChannelHandler;
import com.ddumanskiy.arduino.server.handlers.RegisterChannelHandler;
import com.ddumanskiy.arduino.server.handlers.WorkerChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

/**
 * Channels pipe. Describes execution order of server flow.
 *
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 3:13 PM
 */
public class ChannelsPipe implements ChannelPipelineFactory {

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        return Channels.pipeline(
                //downstream
                new LengthFieldPrepender(2),
                new StringEncoder(),

                //upstream
                new LengthFieldBasedFrameDecoder(Short.MAX_VALUE, 0, 2, 0, 2),
                new StringDecoder(),
                new RegisterChannelHandler(),
                new LoginChannelHandler(),
                new WorkerChannelHandler()
        );
    }
}
