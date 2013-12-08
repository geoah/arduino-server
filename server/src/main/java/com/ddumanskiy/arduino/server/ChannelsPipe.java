package com.ddumanskiy.arduino.server;

import com.ddumanskiy.arduino.common.decoders.MessageIdAndCommand3BytesDecoder;
import com.ddumanskiy.arduino.common.encoders.MessageIdAndCommand3BytesEncoder;
import com.ddumanskiy.arduino.server.handlers.*;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

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
                new MessageIdAndCommand3BytesEncoder(),

                //upstream
                new LengthFieldBasedFrameDecoder(Short.MAX_VALUE, 0, 2, 0, 2),
                new MessageIdAndCommand3BytesDecoder(),

                new RegisterHandler(),
                new PasswordHandler(),
                new LoginHandler(),
                new SaveProfileHandler(),
                new LoadProfileHandler(),
                new ArduinoCommandsHandler()
        );
    }
}
