package com.ddumanskiy.arduino.client;

import com.ddumanskiy.arduino.common.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * User: ddumanskiy
 * Date: 6/15/13
 * Time: 8:14 PM
 */
public class ServerResponsePrinter extends SimpleChannelHandler {

    private static final Logger log = LogManager.getLogger(ServerResponsePrinter.class);

    //it is threadsafe
    private static ObjectMapper mapper = new ObjectMapper()
            .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
            .configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false)
            .configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public ServerResponsePrinter() {

    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Message serverMessage = (Message) e.getMessage();


        ResponseCode responseCode = null;
        try {
            responseCode = mapper.readValue(serverMessage.getBody(), ResponseCode.class);
        } catch (Exception ex) {

        }

        if (responseCode == null || responseCode.getCode() == null) {
            log.info("Server response : {}, ", serverMessage);
        } else {
            log.info("Server response : {}", parserResponseCode(responseCode.getCode()));
        }
    }

    private String parserResponseCode(int responseCode) {
        if (responseCode == 200) {
            return "OK";
        }
        if (responseCode == 2) {
            return "INVALID_COMMAND_FORMAT";
        }
        if (responseCode == 3) {
            return "USER_NOT_REGISTERED";
        }
        if (responseCode == 4) {
            return "USER_ALREADY_REGISTERED";
        }
        if (responseCode == 5) {
            return "USER_NOT_AUTHENTICATED";
        }
        if (responseCode == 6) {
            return "NOT_ALLOWED";
        }
        if (responseCode == 7) {
            return "DEVICE_NOT_IN_NETWORK";
        }
        if (responseCode == 8) {
            return "NOT_SUPPORTED_COMMAND";
        }
        if (responseCode == 9) {
            return "INVALID_TOKEN";
        }
        if (responseCode == 500) {
            return "SERVER_ERROR";
        }

        return "UNKNOWN";
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        log.error(e);
        e.getChannel().close();
    }

}
