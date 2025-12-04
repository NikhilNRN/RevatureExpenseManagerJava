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

import java.util.logging.*;
import java.io.IOException;

public class ManagerExpenseApp
{
    private static final Logger logger = Logger.getLogger(ManagerExpenseApp.class.getName());

    private Scanner scanner;
    private Connection conn;
    private User currentUser;

    // Services
    private AuthenticationService authService;
    private ExpenseService expenseService;

    public ManagerExpenseApp() {
        scanner = new Scanner(System.in);
        setupLogging();
    }

    private void setupLogging() {
        try {
            // Create logs directory if it doesn't exist
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("logs"));

            // File handler - logs to file
            FileHandler fileHandler = new FileHandler("logs/manager_app.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());

            // Console handler - logs to console (only warnings and above)
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.WARNING);

            // Configure logger
            logger.addHandler(fileHandler);
            logger.addHandler(consoleHandler);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);

            logger.info("Logging initialized successfully");
        } catch (IOException e) {
            System.err.println("Failed to setup logging: " + e.getMessage());
        }
    }

    private void initializeServices() throws SQLException {
        logger.info("Initializing database connection and services");
        try {
            conn = DatabaseConnection.getConnection();
            logger.fine("Database connection established");

            // Initialize DAOs
            UserDAO userDAO = new UserDAOImplementation(conn);
            ExpenseDAO expenseDAO = new ExpenseDAOImplementation(conn);
            ApprovalDAO approvalDAO = new ApprovalDAOImplementation(conn);

            // Initialize Services
            authService = new AuthenticationService(userDAO);
            expenseService = new ExpenseService(expenseDAO, approvalDAO);

            logger.info("Services initialized successfully");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to initialize services", e);
            throw e;
        }
    }

    public boolean login() {
        System.out.println("\n=== Manager Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        logger.info("Login attempt for username: " + username);

        currentUser = authService.login(username, password);

        if (currentUser != null) {
            System.out.println("\n✓ Login successful! Welcome, " + currentUser.getUsername());
            logger.info("Login successful for user: " + username + " (ID: " + currentUser.getId() + ")");
            return true;
        } else {
            System.out.println("\n✗ Invalid credentials or not a manager account.");
            logger.warning("Login failed for username: " + username);
            return false;
        }
    }

    public void viewPendingExpenses() {
        logger.info("Viewing pending expenses - User: " + currentUser.getUsername());
        System.out.println("\n=== Pending Expense Reports ===\n");

        List<Expense> expenses = expenseService.getPendingExpenses();

        if (expenses.isEmpty()) {
            System.out.println("No pending expenses to review.");
            logger.fine("No pending expenses found");
            return;
        }

        logger.info("Found " + expenses.size() + " pending expenses");

        // Print table header
        String headerFormat = "%-10s %-20s %-12s %-30s %-12s %-10s%n";
        System.out.format(headerFormat, "ID", "Employee", "Amount", "Description", "Date", "Status");
        System.out.println("─".repeat(104));

        // Print each expense as a table row
        String rowFormat = "%-10s %-20s $%-11.2f %-30s %-12s %-10s%n";
        for (Expense expense : expenses) {
            String description = expense.getDescription();
            if (description.length() > 30) {
                description = description.substring(0, 27) + "...";
            }

            System.out.format(rowFormat,
                    expense.getId(),
                    expense.getEmployeeName(),
                    expense.getAmount(),
                    description,
                    expense.getDate(),
                    expense.getStatus()
            );
        }

        System.out.println("─".repeat(104));
        System.out.println("Total expenses: " + expenses.size());
    }

    public void reviewExpense() {
        System.out.print("\nEnter Expense ID to review: ");

        try {
            int expenseId = Integer.parseInt(scanner.nextLine());
            logger.info("Reviewing expense ID: " + expenseId + " by manager: " + currentUser.getUsername());

            Expense expense = expenseService.getExpenseDetails(expenseId);

            if (expense == null) {
                System.out.println("Expense not found.");
                logger.warning("Expense ID " + expenseId + " not found");
                return;
            }

            logger.fine("Retrieved expense details for ID: " + expenseId);

            // Display expense details in table format
            System.out.println("\n=== Expense Details ===\n");

            String headerFormat = "%-20s %-60s%n";
            System.out.format(headerFormat, "Field", "Value");
            System.out.println("─".repeat(80));

            System.out.format(headerFormat, "Expense ID", expense.getId());
            System.out.format(headerFormat, "Employee", expense.getEmployeeName());
            System.out.format(headerFormat, "Amount", "$" + String.format("%.2f", expense.getAmount()));
            System.out.format(headerFormat, "Description", expense.getDescription());
            System.out.format(headerFormat, "Date", expense.getDate());
            System.out.format(headerFormat, "Current Status", expense.getStatus());

            System.out.println("─".repeat(80));

            System.out.print("\nDecision (1=Approve, 2=Deny, 0=Cancel): ");
            int decision = Integer.parseInt(scanner.nextLine());

            if (decision == 1 || decision == 2) {
                System.out.print("Enter comment: ");
                String comment = scanner.nextLine();

                boolean success;
                if (decision == 1) {
                    logger.info("Attempting to approve expense ID: " + expenseId);
                    success = expenseService.approveExpense(expenseId, currentUser.getId(), comment);
                    if (success) {
                        System.out.println("\n✓ Expense approved successfully!");
                        logger.info("Expense ID " + expenseId + " approved by manager ID: " + currentUser.getId() + " with comment: " + comment);
                    } else {
                        System.out.println("\n✗ Failed to approve expense. Please try again.");
                        logger.warning("Failed to approve expense ID: " + expenseId);
                    }
                } else {
                    logger.info("Attempting to deny expense ID: " + expenseId);
                    success = expenseService.denyExpense(expenseId, currentUser.getId(), comment);
                    if (success) {
                        System.out.println("\n✓ Expense denied successfully!");
                        logger.info("Expense ID " + expenseId + " denied by manager ID: " + currentUser.getId() + " with comment: " + comment);
                    } else {
                        System.out.println("\n✗ Failed to deny expense. Please try again.");
                        logger.warning("Failed to deny expense ID: " + expenseId);
                    }
                }
            } else {
                System.out.println("Review cancelled.");
                logger.info("Review cancelled for expense ID: " + expenseId);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            logger.warning("Invalid input during expense review: " + e.getMessage());
        }
    }

    public void generateReports() {
        logger.info("Generating reports - User: " + currentUser.getUsername());
        System.out.println("\n=== Report Generation ===");
        System.out.println("1. Report by Employee");
        System.out.println("2. Report by Date Range");
        System.out.println("3. Report by Status");
        System.out.print("Select option: ");

        try {
            int option = Integer.parseInt(scanner.nextLine());
            logger.fine("Report option selected: " + option);

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
                    logger.warning("Invalid report option selected: " + option);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            logger.warning("Invalid input during report generation: " + e.getMessage());
        }
    }

    private void reportByEmployee() {
        System.out.print("Enter employee username: ");
        String username = scanner.nextLine();

        logger.info("Generating employee report for: " + username);
        List<Expense> expenses = expenseService.generateEmployeeReport(username);

        System.out.println("\n=== Report for " + username + " ===");
        if (expenses.isEmpty()) {
            System.out.println("No expenses found for this employee.");
            logger.fine("No expenses found for employee: " + username);
            return;
        }

        for (Expense expense : expenses) {
            displayExpenseSummary(expense);
        }

        double total = expenseService.calculateTotal(expenses);
        System.out.println("\nTotal Expenses: $" + String.format("%.2f", total));
        logger.info("Employee report generated for " + username + " - Total: $" + String.format("%.2f", total) + ", Count: " + expenses.size());
    }

    private void reportByDateRange() {
        System.out.print("Start date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine();
        System.out.print("End date (YYYY-MM-DD): ");
        String endDate = scanner.nextLine();

        logger.info("Generating date range report from " + startDate + " to " + endDate);
        List<Expense> expenses = expenseService.generateDateRangeReport(startDate, endDate);

        System.out.println("\n=== Expenses from " + startDate + " to " + endDate + " ===");
        if (expenses.isEmpty()) {
            System.out.println("No expenses found in this date range.");
            logger.fine("No expenses found in date range: " + startDate + " to " + endDate);
            return;
        }

        for (Expense expense : expenses) {
            displayExpenseSummary(expense);
        }

        double total = expenseService.calculateTotal(expenses);
        System.out.println("\nTotal Expenses: $" + String.format("%.2f", total));
        logger.info("Date range report generated - Total: $" + String.format("%.2f", total) + ", Count: " + expenses.size());
    }

    private void reportByStatus() {
        System.out.print("Enter status (pending/approved/denied): ");
        String status = scanner.nextLine();

        logger.info("Generating status report for: " + status);
        List<Expense> expenses = expenseService.generateStatusReport(status);

        System.out.println("\n=== " + status.toUpperCase() + " Expenses ===");
        if (expenses.isEmpty()) {
            System.out.println("No expenses found with this status.");
            logger.fine("No expenses found with status: " + status);
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