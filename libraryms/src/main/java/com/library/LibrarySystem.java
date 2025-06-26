
package com.library;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LibrarySystem extends JFrame {
    private String username;

    public LibrarySystem(String username) {
        this.username = username;
        setTitle("User Dashboard - " + username);
        setLayout(new FlowLayout());
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add functional buttons to the dashboard.
        addButton("View Available Books", e -> showBooks(false));
        addButton("Borrow Book", e -> borrowBook());
        addButton("Return Book", e -> returnBook());
        // Optional: Add a Back button to navigate to the login form.
        addButton("Back", e -> {
            new LoginForm(); // Assumes you have a LoginForm class.
            dispose();
        });
        setVisible(true);
    }

    // Helper method to add buttons with an ActionListener.
    private void addButton(String label, java.awt.event.ActionListener listener) {
        JButton button = new JButton(label);
        button.addActionListener(listener);
        add(button);
    }

    // Static method to add a new book (admin function).
    public static void addBook() {
        JTextField id = new JTextField();
        JTextField title = new JTextField();
        JTextField author = new JTextField();

        Object[] fields = {"ID:", id, "Title:", title, "Author:", author};
        int result = JOptionPane.showConfirmDialog(null, fields, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("INSERT INTO books (id, title, author) VALUES (?, ?, ?)")) {
                ps.setString(1, id.getText());
                ps.setString(2, title.getText());
                ps.setString(3, author.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Book added!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Static method to delete a book (admin function).
    public static void deleteBook() {
        String id = JOptionPane.showInputDialog("Enter Book ID to delete:");
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM books WHERE id = ?")) {
            ps.setString(1, id);
            int deleted = ps.executeUpdate();
            JOptionPane.showMessageDialog(null, deleted > 0 ? "Book deleted." : "Book not found.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Static method to show books. If 'all' is true, it shows all books; otherwise, it shows only available books.
    public static void showBooks(boolean all) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(all 
                     ? "SELECT * FROM books" 
                     : "SELECT * FROM books WHERE isBorrowed = FALSE");
             ResultSet rs = ps.executeQuery()) {
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                Book book = new Book(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("isBorrowed"),
                        rs.getString("borrowedBy")
                );
                sb.append(book.toString()).append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.length() > 0 ? sb.toString() : "No books found.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Instance method for borrowing a book.
    private void borrowBook() {
        String id = JOptionPane.showInputDialog("Enter Book ID to borrow:");
        if (id == null || id.trim().isEmpty()) {
            return;
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE books SET isBorrowed = TRUE, borrowedBy = ? WHERE id = ? AND isBorrowed = FALSE")) {
            ps.setString(1, username);
            ps.setString(2, id);
            int updated = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, updated > 0 ? "Book borrowed!" : "Book not available.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Instance method for returning a book.
    private void returnBook() {
        String id = JOptionPane.showInputDialog("Enter Book ID to return:");
        if (id == null || id.trim().isEmpty()) {
            return;
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE books SET isBorrowed = FALSE, borrowedBy = NULL WHERE id = ? AND borrowedBy = ?")) {
            ps.setString(1, id);
            ps.setString(2, username);
            int updated = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, updated > 0 ? "Book returned!" : "You have not borrowed this book.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
