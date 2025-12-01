package com.expense.manager.dao;

import com.expense.manager.model.Expense;

import java.util.List;

public interface ExpenseDAO
{
    List<Expense> getPendingExpenses() throws Exception;
    Expense getExpenseById(int id) throws Exception;
    List<Expense> getExpensesByEmployee(String username) throws Exception;
    List<Expense> getExpensesByDateRange(String startDate, String endDate) throws Exception;
    List<Expense> getExpensesByStatus(String status) throws Exception;
}
