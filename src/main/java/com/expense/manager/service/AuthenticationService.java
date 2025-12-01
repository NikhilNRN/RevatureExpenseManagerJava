package com.expense.manager.service;

import com.expense.manager.dao.UserDAO;
import com.expense.manager.model.User;

public class AuthenticationService {
    private UserDAO userDAO;

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String username, String password) {
        try {
            return userDAO.authenticate(username, password);
        } catch (Exception e) {
            System.err.println("Service Error - Authentication failed: " + e.getMessage());
            return null;
        }
    }
}