package com.expense.manager.model;

public class Approval
{
    private int id;
    private int expenseId;
    private String status;
    private Integer reviewerId;
    private String comment;
    private String reviewDate;

    public Approval(int id, int expenseId, String status) {
        this.id = id;
        this.expenseId = expenseId;
        this.status = status;
    }

    public int getId() { return id; }
    public int getExpenseId() { return expenseId; }
    public String getStatus() { return status; }
    public Integer getReviewerId() { return reviewerId; }
    public String getComment() { return comment; }
    public String getReviewDate() { return reviewDate; }

    public void setStatus(String status) { this.status = status; }
    public void setReviewerId(Integer reviewerId) { this.reviewerId = reviewerId; }
    public void setComment(String comment) { this.comment = comment; }
    public void setReviewDate(String date) { this.reviewDate = date; }
}
