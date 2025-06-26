package com.library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookDAO {

    // ------------------ AVAILABLE BOOKS ------------------
    public static DefaultTableModel getAvailableBooksTableModel() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Title", "Author"}, 0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, title, author FROM books WHERE isBorrowed = FALSE");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id"),
                    rs.getString("title"),
                    rs.getString("author")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    // ------------------ BORROWED BOOKS FOR USER ------------------
    public static DefaultTableModel getBorrowedBooksTableModel(String username) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Due Date"}, 0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT id, title, author, due_date FROM books WHERE isBorrowed = TRUE AND borrowedBy = ?")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String dueDateStr = (rs.getDate("due_date") != null) ? rs.getDate("due_date").toString() : "";
                model.addRow(new Object[]{
                    rs.getString("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    dueDateStr
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    // ------------------ BORROW BOOK ------------------
    public static void borrowBook(JTable table, String username) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a book to borrow.");
            return;
        }

        String bookId = table.getValueAt(selectedRow, 0).toString();

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement checkStmt = con.prepareStatement("SELECT isBorrowed FROM books WHERE id = ?");
            checkStmt.setString(1, bookId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getBoolean("isBorrowed")) {
                JOptionPane.showMessageDialog(null, "This book is already borrowed.");
                return;
            }

            LocalDate dueDate = LocalDate.now().plusDays(14);
            Date sqlDueDate = Date.valueOf(dueDate);

            PreparedStatement ps = con.prepareStatement(
                "UPDATE books SET isBorrowed = TRUE, borrowedBy = ?, due_date = ? WHERE id = ?");
            ps.setString(1, username);
            ps.setDate(2, sqlDueDate);
            ps.setString(3, bookId);

            int updated = ps.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(null, "Book borrowed successfully! Due Date: " + dueDate);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to borrow the book.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error borrowing book: " + e.getMessage());
        }
    }

    // ------------------ RETURN BOOK ------------------
    public static void returnBook(JTable table, String username) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a book to return.");
            return;
        }

        String bookId = table.getValueAt(selectedRow, 0).toString();

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement checkStmt = con.prepareStatement(
                "SELECT due_date FROM books WHERE id = ? AND borrowedBy = ?");
            checkStmt.setString(1, bookId);
            checkStmt.setString(2, username);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(null, "Book not found or not borrowed by you.");
                return;
            }

            Date due = rs.getDate("due_date");
            LocalDate dueDate = (due != null) ? due.toLocalDate() : LocalDate.now();
            LocalDate now = LocalDate.now();
            long fine = 0;

            if (now.isAfter(dueDate)) {
                fine = ChronoUnit.DAYS.between(dueDate, now);
            }

            PreparedStatement returnStmt = con.prepareStatement(
                "UPDATE books SET isBorrowed = FALSE, borrowedBy = NULL, due_date = NULL WHERE id = ?");
            returnStmt.setString(1, bookId);

            int updated = returnStmt.executeUpdate();
            if (updated > 0) {
                String msg = "Book returned successfully.";
                if (fine > 0) msg += " Late fine: $" + fine;
                JOptionPane.showMessageDialog(null, msg);
            } else {
                JOptionPane.showMessageDialog(null, "Return failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error returning book: " + e.getMessage());
        }
    }

    // ------------------ ADMIN: ALL BOOKS ------------------
    public static DefaultTableModel getBooksTableModel() {
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Title", "Author", "Borrower", "Due Date", "Fine"}, 0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, title, author, borrowedBy, due_date FROM books");
             ResultSet rs = ps.executeQuery()) {

            LocalDate today = LocalDate.now();

            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String borrower = rs.getString("borrowedBy");
                Date due = rs.getDate("due_date");

                String dueDateStr = (due != null) ? due.toString() : "";
                long fine = 0;
                if (due != null && today.isAfter(due.toLocalDate())) {
                    fine = ChronoUnit.DAYS.between(due.toLocalDate(), today);
                }

                model.addRow(new Object[]{id, title, author, borrower, dueDateStr, "$" + fine});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    // ------------------ ADMIN: ADD BOOK ------------------
    public static void addBookPrompt(JFrame parent) {
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();

        Object[] message = {
            "Book ID:", idField,
            "Title:", titleField,
            "Author:", authorField
        };

        int option = JOptionPane.showConfirmDialog(parent, message, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO books (id, title, author, isBorrowed) VALUES (?, ?, ?, FALSE)")) {
                ps.setString(1, idField.getText());
                ps.setString(2, titleField.getText());
                ps.setString(3, authorField.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(parent, "Book added successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parent, "Error adding book: " + e.getMessage());
            }
        }
    }
    

    // ------------------ ADMIN: DELETE BOOK ------------------
    public static void deleteSelectedBook(JTable table, JFrame parent) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a book to delete.");
            return;
        }

        String bookId = table.getValueAt(selectedRow, 0).toString();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM books WHERE id = ?")) {
            ps.setString(1, bookId);
            int deleted = ps.executeUpdate();
            if (deleted > 0) {
                JOptionPane.showMessageDialog(parent, "Book deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(parent, "Book deletion failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Error deleting book: " + e.getMessage());
        }
    }
}
