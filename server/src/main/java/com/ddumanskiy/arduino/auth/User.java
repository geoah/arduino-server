package com.ddumanskiy.arduino.auth;

import com.ddumanskiy.arduino.model.UserProfile;
import com.ddumanskiy.arduino.utils.JsonParser;

import java.io.Serializable;

/**
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 4:03 PM
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String pass;

    private String id;

    private UserProfile userProfile;

    public User() {
    }

    public User(String name, String pass, String id) {
        this.name = name;
        this.pass = pass;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (name != null ? !name.equals(user.name) : user.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
