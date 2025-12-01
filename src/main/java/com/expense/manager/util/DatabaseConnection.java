package com.expense.manager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection
{
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expense_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    public static Connection getConnection() throws SQLException
    {
        try
        {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e)
        {
            System.err.println("Database connection failed: " + e.getMessage());
            throw e;
        }
    }
}