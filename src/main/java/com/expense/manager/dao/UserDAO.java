package com.expense.manager.dao;

import com.expense.manager.model.User;

public interface UserDAO
{
    User authenticate(String username, String password) throws Exception;
    User getUserById(int id) throws Exception;
}
