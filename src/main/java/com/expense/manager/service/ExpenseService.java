package com.expense.manager.service;

import com.expense.manager.dao.ApprovalDAO;
import com.expense.manager.dao.ExpenseDAO;
import com.expense.manager.model.Expense;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseService {
    private ExpenseDAO expenseDAO;
    private ApprovalDAO approvalDAO;

    public ExpenseService(ExpenseDAO expenseDAO, ApprovalDAO approvalDAO) {
        this.expenseDAO = expenseDAO;
        this.approvalDAO = approvalDAO;
    }

    public List<Expense> getPendingExpenses() {
        try {
            return expenseDAO.getPendingExpenses();
        } catch (Exception e) {
            System.err.println("Service Error - Failed to retrieve pending expenses: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Expense getExpenseDetails(int expenseId) {
        try {
            return expenseDAO.getExpenseById(expenseId);
        } catch (Exception e) {
            System.err.println("Service Error - Failed to retrieve expense: " + e.getMessage());
            return null;
        }
    }

    public boolean approveExpense(int expenseId, int managerId, String comment) {
        try {
            approvalDAO.updateApproval(expenseId, "approved", managerId, comment, LocalDate.now().toString());
            return true;
        } catch (Exception e) {
            System.err.println("Service Error - Failed to approve expense: " + e.getMessage());
            return false;
        }
    }

    public boolean denyExpense(int expenseId, int managerId, String comment) {
        try {
            approvalDAO.updateApproval(expenseId, "denied", managerId, comment, LocalDate.now().toString());
            return true;
        } catch (Exception e) {
            System.err.println("Service Error - Failed to deny expense: " + e.getMessage());
            return false;
        }
    }

    public List<Expense> generateEmployeeReport(String username) {
        try {
            return expenseDAO.getExpensesByEmployee(username);
        } catch (Exception e) {
            System.err.println("Service Error - Failed to generate employee report: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Expense> generateDateRangeReport(String startDate, String endDate) {
        try {
            return expenseDAO.getExpensesByDateRange(startDate, endDate);
        } catch (Exception e) {
            System.err.println("Service Error - Failed to generate date range report: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Expense> generateStatusReport(String status) {
        try {
            return expenseDAO.getExpensesByStatus(status);
        } catch (Exception e) {
            System.err.println("Service Error - Failed to generate status report: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public double calculateTotal(List<Expense> expenses) {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }
}