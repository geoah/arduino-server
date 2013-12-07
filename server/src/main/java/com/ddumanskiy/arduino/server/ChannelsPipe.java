package com.ddumanskiy.arduino.server;

import com.ddumanskiy.arduino.common.decoders.MessageIdDecoder;
import com.ddumanskiy.arduino.common.encoders.MessageIdEncoder;
import com.ddumanskiy.arduino.server.handlers.*;
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

    private static final int MESSAGE_ID_FIELD_LENGTH_BYTES = 2;

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        return Channels.pipeline(
                //downstream
                new LengthFieldPrepender(2),
                new MessageIdEncoder(MESSAGE_ID_FIELD_LENGTH_BYTES),
                new StringEncoder(),

                //upstream
                new LengthFieldBasedFrameDecoder(Short.MAX_VALUE, 0, 2, 0, 2),
                new MessageIdDecoder(MESSAGE_ID_FIELD_LENGTH_BYTES),
                new StringDecoder(),
                new RegisterChannelHandler(),
                new LoginChannelHandler(),
                new SaveProfileHandler(),
                new LoadProfileHandler(),
                new WorkerChannelHandler()
        );
    }
}
