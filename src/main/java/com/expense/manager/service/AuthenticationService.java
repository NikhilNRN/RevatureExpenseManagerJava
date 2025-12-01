package com.expense.manager.service;

import com.expense.manager.dao.UserDAO;
import com.expense.manager.model.User;

public class AuthenticationService {
    private UserDAO userDAO;

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
