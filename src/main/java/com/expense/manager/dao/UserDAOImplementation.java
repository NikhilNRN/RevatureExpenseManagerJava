package com.expense.manager.dao;

import com.expense.manager.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImplementation implements UserDAO {
    private Connection conn;

    public UserDAOImplementation(Connection conn) {
        this.conn = conn;
    }

    @Override
    public User authenticate(String username, String password) throws Exception {
        String query = "SELECT id, username, password, role FROM users WHERE username = ? AND password = ? AND role = 'Manager'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
            return null;
        }
    }

    @Override
    public User getUserById(int id) throws Exception {
        String query = "SELECT id, username, password, role FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
            return null;
        }
    }
}
