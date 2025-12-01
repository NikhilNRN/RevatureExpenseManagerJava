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

    @Override
    public List<Expense> getPendingExpenses() throws Exception {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT e.id, e.user_id, e.amount, e.description, e.date, u.username, a.status " +
                "FROM expenses e " +
                "JOIN users u ON e.user_id = u.id " +
                "JOIN approvals a ON e.id = a.expense_id " +
                "WHERE a.status = 'pending' " +
                "ORDER BY e.date DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getString("date")
                );
                expense.setEmployeeName(rs.getString("username"));
                expense.setStatus(rs.getString("status"));
                expenses.add(expense);
            }
        }
        return expenses;
    }

    @Override
    public Expense getExpenseById(int id) throws Exception {
        String query = "SELECT e.*, u.username, a.status " +
                "FROM expenses e " +
                "JOIN users u ON e.user_id = u.id " +
                "JOIN approvals a ON e.id = a.expense_id " +
                "WHERE e.id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Expense expense = new Expense(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getString("date")
                );
                expense.setEmployeeName(rs.getString("username"));
                expense.setStatus(rs.getString("status"));
                return expense;
            }
            return null;
        }
    }

    @Override
    public List<Expense> getExpensesByEmployee(String username) throws Exception {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT e.id, e.user_id, e.amount, e.description, e.date, a.status " +
                "FROM expenses e " +
                "JOIN users u ON e.user_id = u.id " +
                "JOIN approvals a ON e.id = a.expense_id " +
                "WHERE u.username = ? " +
                "ORDER BY e.date DESC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getString("date")
                );
                expense.setEmployeeName(username);
                expense.setStatus(rs.getString("status"));
                expenses.add(expense);
            }
        }
        return expenses;
    }

    @Override
    public List<Expense> getExpensesByDateRange(String startDate, String endDate) throws Exception {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT e.id, e.user_id, e.amount, e.description, e.date, u.username, a.status " +
                "FROM expenses e " +
                "JOIN users u ON e.user_id = u.id " +
                "JOIN approvals a ON e.id = a.expense_id " +
                "WHERE e.date BETWEEN ? AND ? " +
                "ORDER BY e.date DESC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getString("date")
                );
                expense.setEmployeeName(rs.getString("username"));
                expense.setStatus(rs.getString("status"));
                expenses.add(expense);
            }
        }
        return expenses;
    }

    @Override
    public List<Expense> getExpensesByStatus(String status) throws Exception {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT e.id, e.user_id, e.amount, e.description, e.date, u.username, a.status " +
                "FROM expenses e " +
                "JOIN users u ON e.user_id = u.id " +
                "JOIN approvals a ON e.id = a.expense_id " +
                "WHERE a.status = ? " +
                "ORDER BY e.date DESC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getString("date")
                );
                expense.setEmployeeName(rs.getString("username"));
                expense.setStatus(rs.getString("status"));
                expenses.add(expense);
            }
        }
        return expenses;
    }
}