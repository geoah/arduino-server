package com.ddumanskiy.arduino.utils;

import com.ddumanskiy.arduino.auth.User;
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

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Error jsoning object.");
            log.error(e);
        }
        return "{}";
    }

    public static User parseUser(String reader) {
        try {
            return mapper.reader(User.class).readValue(reader);
        } catch (IOException e) {
            log.error("Error parsing input string : {}", reader);
            log.error(e);
        }
        return null;
    }

    public static UserProfile parseProfile(String reader) {
        try {
            return mapper.reader(UserProfile.class).readValue(reader);
        } catch (IOException e) {
            log.error("Error parsing input string : {}", reader);
            log.error(e);
        }
        return null;
    }

    public static UserProfile parseProfile(InputStream reader) {
        try {
            return mapper.reader(UserProfile.class).readValue(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
