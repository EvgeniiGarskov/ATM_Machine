package com.example.atm_machine;

import com.example.atm_machine.Models.User;

public class UserSession {

    private static UserSession userSession;

    private User currentUser;

    private UserSession() {
    }

    public static UserSession getUserSession() {

        if (userSession == null) {
            userSession = new UserSession();
        }
        return userSession;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;

    }
}
