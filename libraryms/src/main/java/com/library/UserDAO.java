package com.library;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class UserDAO {

    public static boolean validateLogin(String username, String password, String role) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void createUser(String username, String password) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO users (username, password, role) VALUES (?, ?, 'user')")) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Revised getUserBookStatus method. The query retrieves the book title, the user who borrowed it, and its due date.
    // A fine is calculated as ₹1 per day if the book is returned past its due date.
    public static DefaultTableModel getUserBookStatus() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Borrower", "Book Title", "Due Date", "Fine"}, 0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT title, borrowedBy, duedate FROM books WHERE isBorrowed = TRUE");
             ResultSet rs = ps.executeQuery()) {

            LocalDate today = LocalDate.now();
            while (rs.next()) {
                String title = rs.getString("title");
                String borrower = rs.getString("borrowedBy");
                Date due = rs.getDate("duedate");
                String dueDateStr = (due != null ? due.toString() : "");
                long fine = 0;
                if (due != null) {
                    LocalDate dueDate = due.toLocalDate();
                    if (today.isAfter(dueDate)) {
                        fine = ChronoUnit.DAYS.between(dueDate, today);
                    }
                }
                model.addRow(new Object[]{borrower, title, dueDateStr, "₹" + fine});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }
}
