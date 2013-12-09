package com.ddumanskiy.arduino.utils;

import com.ddumanskiy.arduino.auth.User;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * User: ddumanskiy
 * Date: 09.12.13
 * Time: 8:07
 */
public class FileManagerIntegrationTest {

    private User user1 = new User("name1", "pass1", "id1");
    private User user2 = new User("name2", "pass2", "id2");

    @Before
    public void cleanup() throws IOException {
        Path file;
        file = FileManager.generateFileName(user1.getName());
        Files.deleteIfExists(file);

        file = FileManager.generateFileName(user2.getName());
        Files.deleteIfExists(file);
    }

    @Test
    public void testGenerateFileName() {
        Path file = FileManager.generateFileName(user1.getName());
        assertEquals("u_name1.user", file.getFileName().toString());
    }

    @Test
    public void testCreationTempFile() throws IOException {
        assertTrue(FileManager.saveNewUserToFile(user1));
        //file existence ignored
        assertTrue(FileManager.saveNewUserToFile(user1));
    }

    @Test
    public void testReadListOfFiles() {
        assertTrue(FileManager.saveNewUserToFile(user1));
        assertTrue(FileManager.saveNewUserToFile(user2));

        ConcurrentHashMap<String, User> users = FileManager.deserialize();
        assertNotNull(users);
        assertNotNull(users.get(user1.getName()));
        assertNotNull(users.get(user2.getName()));
    }

    @Test
    public void testOverrideFiles() {
        assertTrue(FileManager.overrideUserFile(user1));
        assertTrue(FileManager.overrideUserFile(user1));

        ConcurrentHashMap<String, User> users = FileManager.deserialize();
        assertNotNull(users);
        assertNotNull(users.get(user1.getName()));
    }

}
