package com.ddumanskiy.arduino.utils;

import com.ddumanskiy.arduino.model.UserProfile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 15:31
 */
public final class JsonParser {

    private static final Logger log = LogManager.getLogger(JsonParser.class);

    //it is threadsafe
    private static ObjectMapper mapper = new ObjectMapper()
            .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
            .configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);

    private JsonParser() {

    }

    public static String toJson(UserProfile userProfile) {
        try {
            return mapper.writeValueAsString(userProfile);
        } catch (Exception e) {
            log.error("Error loading user profile.");
            log.error(e);
        }
        return "{}";
    }

    public static UserProfile parse(String reader) {
        try {
            return mapper.reader(UserProfile.class).readValue(reader);
        } catch (IOException e) {
            log.error("Error parsing input string : {}", reader);
            log.error(e);
        }
        return null;
    }

    public static UserProfile parse(InputStream reader) {
        try {
            return mapper.reader(UserProfile.class).readValue(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
