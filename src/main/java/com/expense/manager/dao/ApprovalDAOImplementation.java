package com.expense.manager.dao;


import com.expense.manager.model.Approval;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ApprovalDAOImplementation implements ApprovalDAO
{
    private Connection conn;

    public ApprovalDAOImplementation(Connection conn)
    {
        this.conn = conn;
    }

    @Override
    public Approval getApprovalByExpenseId(int expenseId) throws Exception
    {
        String query = "SELECT * FROM approvals WHERE expense_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query))
        {
            stmt.setInt(1, expenseId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Approval approval = new Approval(
                        rs.getInt("id"),
                        rs.getInt("expense_id"),
                        rs.getString("status")
                );
                approval.setReviewerId(rs.getObject("reviewer", Integer.class));
                approval.setComment(rs.getString("comment"));
                approval.setReviewDate(rs.getString("review_date"));
                return approval;
            }
            return null;
        }
    }

    @Override
    public void updateApproval(int expenseId, String status, int reviewerId, String comment, String reviewDate) throws Exception
    {
        String query = "UPDATE approvals SET status = ?, reviewer = ?, comment = ?, review_date = ? WHERE expense_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query))
        {
            stmt.setString(1, status);
            stmt.setInt(2, reviewerId);
            stmt.setString(3, comment);
            stmt.setString(4, reviewDate);
            stmt.setInt(5, expenseId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0)
            {
                throw new Exception("No approval found for expense ID: " + expenseId);
            }
        }
    }
}