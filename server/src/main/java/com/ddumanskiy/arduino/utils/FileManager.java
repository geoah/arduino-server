package com.ddumanskiy.arduino.utils;

import com.ddumanskiy.arduino.auth.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 6:53 PM
 */
public final class FileManager {

    private static final Logger log = LogManager.getLogger(FileManager.class);

    private static final Charset charset = Charset.forName("US-ASCII");
    private static final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

    private FileManager() {
    }

    public static Path generateFileName(String userName) {
        return Paths.get(System.getProperty("java.io.tmpdir"), "u_" + userName + ".user");
    }

    /**
     * Returns true if user was successfully saved to file.
     * @param user - user to save
     * @return true in case of success
     */
    public static boolean saveUserToFile(User user) {
        Path tempFile;
        try {
            Path file = generateFileName(user.getName());
            tempFile = Files.createFile(file);
        } catch (FileAlreadyExistsException fae) {
            log.error("File already exists. Should never happen. User : {}", user);
            return false;
        } catch (IOException ioe) {
            log.error("Error creating temp file.", ioe);
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile, charset)) {
            writer.write(user.toString());
        } catch (IOException ioe) {
            log.error("Error writing temp file.", ioe);
            return false;
        }

        return true;
    }

    public static ConcurrentHashMap<String, User> deserialize() {
        Finder finder = new Finder("u_*.user");


        try {
            Files.walkFileTree(tempDir, finder);
        } catch (IOException e) {
            log.error("Error reading tmp files.", e);
        }

        ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
        for (Path path : finder.getFoundFiles()) {
            User user = readUserFromFile(path);
            if (user != null) {
                users.putIfAbsent(user.getName(), user);
            }
        }

        return users;
    }

    private static User readUserFromFile(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String userString = reader.readLine();
            return JsonParser.parseUser(userString);
        } catch (IOException ioe) {
            log.error("Error reading temp file.", ioe);
        }

        return null;
    }

}
