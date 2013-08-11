package com.ddumanskiy.arduino.utils;

import com.ddumanskiy.arduino.user.User;
import org.apache.commons.lang.SerializationUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 6:53 PM
 */
public class Serializer {

    private static final String USER_DB = "user.db";
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String USER_DB_FILE_TEMP_DIR =
            System.getProperty("java.io.tmpdir").endsWith(FILE_SEPARATOR) ?
                    System.getProperty("java.io.tmpdir") :
                    System.getProperty("java.io.tmpdir") + FILE_SEPARATOR;

    /**
     * Serializes object to temp dir. In file "user.db".
     * @param object - to be serialized.
     */
    public static void serialize(ConcurrentHashMap object) {
        try {
            //should be closed within SerializationUtils.
            FileOutputStream fos = new FileOutputStream(USER_DB_FILE_TEMP_DIR + USER_DB);
            SerializationUtils.serialize(object, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    public static ConcurrentHashMap<String, User> deserialize() {
        ConcurrentHashMap<String, User> map = null;
        try {
            FileInputStream fis = new FileInputStream(USER_DB_FILE_TEMP_DIR + USER_DB);
            map = (ConcurrentHashMap<String, User>) SerializationUtils.deserialize(fis);
        } catch (FileNotFoundException e) {
            //not required to have this file, so ignoring.
        }

        if (map == null) {
            return new ConcurrentHashMap<String, User>();
        }

        return map;
    }

}
