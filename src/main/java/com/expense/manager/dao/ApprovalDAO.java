package com.expense.manager.dao;

public interface ApprovalDAO
{
    Approval getApprovalByExpenseId(int expenseId) throws Exception;
    void updateApproval(int expenseId, String status, int reviewerId, String comment, String reviewDate) throws Exception;
}
