package com.dogesuck.model;

import android.content.Context;

import org.json.JSONObject;

public class User {
    private String username;
    private String password;
    private String usernameDoge;
    private String passwordDoge;
    private String session;
    private String sessionDoge;
    private static SessionModel sessionModel;

    public User(Context context) {
        sessionModel = new SessionModel(context);
        try {
            JSONObject jsonObject = sessionModel.get();
            setUsername((String) jsonObject.get("username"));
            setPassword((String) jsonObject.get("password"));
            setSession((String) jsonObject.get("session"));
            setUsernameDoge((String) jsonObject.get("usernameDoge"));
            setPasswordDoge((String) jsonObject.get("passwordDoge"));
            setSessionDoge((String) jsonObject.get("sessionDoge"));
        } catch (Exception e) {
            setUsername("");
            setPassword("");
            setSession("");
            setUsernameDoge("");
            setPasswordDoge("");
            setSessionDoge("");
        }
    }

    public void save(String value) {
        sessionModel.save(value);
    }

    public void clear() {
        sessionModel.clear();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsernameDoge() {
        return usernameDoge;
    }

    public void setUsernameDoge(String usernameDoge) {
        this.usernameDoge = usernameDoge;
    }

    public String getPasswordDoge() {
        return passwordDoge;
    }

    public void setPasswordDoge(String passwordDoge) {
        this.passwordDoge = passwordDoge;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSessionDoge() {
        return sessionDoge;
    }

    public void setSessionDoge(String sessionDoge) {
        this.sessionDoge = sessionDoge;
    }
}
