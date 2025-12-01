package com.expense.manager.model;

public class Expense
{
    private int id;
    private int userId;
    private double amount;
    private String description;
    private String date;
    private String employeeName;
    private String status;

    public Expense(int id, int userId, double amount, String description, String date) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }
