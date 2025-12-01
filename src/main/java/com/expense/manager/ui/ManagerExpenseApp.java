package com.expense.manager.ui;

import com.expense.manager.dao.*;
import com.expense.manager.model.Expense;
import com.expense.manager.model.User;
import com.expense.manager.service.AuthenticationService;
import com.expense.manager.service.ExpenseService;
import com.expense.manager.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ManagerExpenseApp {
    private Scanner scanner;
    private Connection conn;
    private User currentUser;

    // Services
    private AuthenticationService authService;
    private ExpenseService expenseService;

    public ManagerExpenseApp() {
        scanner = new Scanner(System.in);
    }

    private void initializeServices() throws SQLException {
        conn = DatabaseConnection.getConnection();

        // Initialize DAOs
        UserDAO userDAO = new UserDAOImplementation(conn);
        ExpenseDAO expenseDAO = new ExpenseDAOImplementation(conn);
        ApprovalDAO approvalDAO = new ApprovalDAOImplementation(conn);

        // Initialize Services
        authService = new AuthenticationService(userDAO);
        expenseService = new ExpenseService(expenseDAO, approvalDAO);
    }

    public boolean login() {
        System.out.println("\n=== Manager Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUser = authService.login(username, password);

        if (currentUser != null) {
            System.out.println("\n✓ Login successful! Welcome, " + currentUser.getUsername());
            return true;
        } else {
            System.out.println("\n✗ Invalid credentials or not a manager account.");
            return false;
        }
    }

    public void viewPendingExpenses() {
        System.out.println("\n=== Pending Expense Reports ===");

        List<Expense> expenses = expenseService.getPendingExpenses();

        if (expenses.isEmpty()) {
            System.out.println("No pending expenses to review.");
            return;
        }

        for (Expense expense : expenses) {
            System.out.println("\n--- Expense ID: " + expense.getId() + " ---");
            System.out.println("Employee: " + expense.getEmployeeName());
            System.out.println("Amount: $" + String.format("%.2f", expense.getAmount()));
            System.out.println("Description: " + expense.getDescription());
            System.out.println("Date: " + expense.getDate());
            System.out.println("Status: " + expense.getStatus());
            System.out.println("------------------------");
        }
    }

    public void reviewExpense() {
        System.out.print("\nEnter Expense ID to review: ");

        try {
            int expenseId = Integer.parseInt(scanner.nextLine());
            Expense expense = expenseService.getExpenseDetails(expenseId);

            if (expense == null) {
                System.out.println("Expense not found.");
                return;
            }

            System.out.println("\n=== Expense Details ===");
            System.out.println("Employee: " + expense.getEmployeeName());
            System.out.println("Amount: $" + String.format("%.2f", expense.getAmount()));
            System.out.println("Description: " + expense.getDescription());
            System.out.println("Date: " + expense.getDate());
            System.out.println("Current Status: " + expense.getStatus());

            System.out.print("\nDecision (1=Approve, 2=Deny, 0=Cancel): ");
            int decision = Integer.parseInt(scanner.nextLine());

            if (decision == 1 || decision == 2) {
                System.out.print("Enter comment: ");
                String comment = scanner.nextLine();

                boolean success;
                if (decision == 1) {
                    success = expenseService.approveExpense(expenseId, currentUser.getId(), comment);
                    if (success) {
                        System.out.println("\n✓ Expense approved successfully!");
                    } else {
                        System.out.println("\n✗ Failed to approve expense. Please try again.");
                    }
                } else {
                    success = expenseService.denyExpense(expenseId, currentUser.getId(), comment);
                    if (success) {
                        System.out.println("\n✓ Expense denied successfully!");
                    } else {
                        System.out.println("\n✗ Failed to deny expense. Please try again.");
                    }
                }
            } else {
                System.out.println("Review cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    public void generateReports() {
        System.out.println("\n=== Report Generation ===");
        System.out.println("1. Report by Employee");
        System.out.println("2. Report by Date Range");
        System.out.println("3. Report by Status");
        System.out.print("Select option: ");

        try {
            int option = Integer.parseInt(scanner.nextLine());

            switch (option) {
                case 1:
                    reportByEmployee();
                    break;
                case 2:
                    reportByDateRange();
                    break;
                case 3:
                    reportByStatus();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    private void reportByEmployee() {
        System.out.print("Enter employee username: ");
        String username = scanner.nextLine();

        List<Expense> expenses = expenseService.generateEmployeeReport(username);

        System.out.println("\n=== Report for " + username + " ===");
        if (expenses.isEmpty()) {
            System.out.println("No expenses found for this employee.");
            return;
        }

        for (Expense expense : expenses) {
            displayExpenseSummary(expense);
        }

        double total = expenseService.calculateTotal(expenses);
        System.out.println("\nTotal Expenses: $" + String.format("%.2f", total));
    }

    private void reportByDateRange() {
        System.out.print("Start date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine();
        System.out.print("End date (YYYY-MM-DD): ");
        String endDate = scanner.nextLine();

        List<Expense> expenses = expenseService.generateDateRangeReport(startDate, endDate);

        System.out.println("\n=== Expenses from " + startDate + " to " + endDate + " ===");
        if (expenses.isEmpty()) {
            System.out.println("No expenses found in this date range.");
            return;
        }

        for (Expense expense : expenses) {
            displayExpenseSummary(expense);
        }

        double total = expenseService.calculateTotal(expenses);
        System.out.println("\nTotal Expenses: $" + String.format("%.2f", total));
    }

    private void reportByStatus() {
        System.out.print("Enter status (pending/approved/denied): ");
        String status = scanner.nextLine();

        List<Expense> expenses = expenseService.generateStatusReport(status);

        System.out.println("\n=== " + status.toUpperCase() + " Expenses ===");
        if (expenses.isEmpty()) {
            System.out.println("No expenses found with this status.");
            return;
        }

        for (Expense expense : expenses) {
            displayExpenseSummary(expense);
        }

        double total = expenseService.calculateTotal(expenses);
        System.out.println("\nTotal Expenses: $" + String.format("%.2f", total));
    }

    private void displayExpenseSummary(Expense expense) {
        System.out.println("\nExpense ID: " + expense.getId());
        System.out.println("Employee: " + expense.getEmployeeName());
        System.out.println("Amount: $" + String.format("%.2f", expense.getAmount()));
        System.out.println("Description: " + expense.getDescription());
        System.out.println("Date: " + expense.getDate());
        System.out.println("Status: " + expense.getStatus());
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== Manager Expense Management System ===");
            System.out.println("1. View Pending Expenses");
            System.out.println("2. Review Expense");
            System.out.println("3. Generate Reports");
            System.out.println("4. Logout");
            System.out.print("Select option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        viewPendingExpenses();
                        break;
                    case 2:
                        reviewExpense();
                        break;
                    case 3:
                        generateReports();
                        break;
                    case 4:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void cleanup() {
        try {
            if (conn != null && !conn.isClosed())
            {
                conn.close();
            }
            scanner.close();
        } catch (SQLException e)
        {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ManagerExpenseApp app = new ManagerExpenseApp();

        try {
            app.initializeServices();

            if (app.login()) {
                app.showMenu();
            }
        } catch (SQLException e) {
            System.err.println("Application Error: Database connection failed - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Application Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            app.cleanup();
        }
    }
}