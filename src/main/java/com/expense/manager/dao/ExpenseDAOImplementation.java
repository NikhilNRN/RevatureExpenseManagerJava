package com.expense.manager.dao;

import com.expense.manager.model.Expense;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAOImplementation implements ExpenseDAO
{
    private Connection conn;

    public ExpenseDAOImplementation(Connection conn)
    {
        this.conn = conn;
    }
