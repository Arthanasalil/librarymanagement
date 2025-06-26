package com.library;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminPanel extends JFrame {
    String username;

    public AdminPanel(String username) {
        this.username = username;
        setTitle("Admin Panel");
        setLayout(new FlowLayout());
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addButton("Add Book", e -> LibrarySystem.addBook());
        addButton("Delete Book", e -> LibrarySystem.deleteBook());
        addButton("View All Books", e -> LibrarySystem.showBooks(true));
        addButton("Confirm Return", e -> confirmReturn());
        setVisible(true);
    }

    private void addButton(String label, java.awt.event.ActionListener listener) {
        JButton button = new JButton(label);
        button.addActionListener(listener);
        add(button);
    }

    private void confirmReturn() {
        String id = JOptionPane.showInputDialog("Enter Book ID to confirm return:");
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE books SET isBorrowed = FALSE, borrowedBy = NULL WHERE id = ?");
            ps.setString(1, id);
            int updated = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, updated > 0 ? "Return confirmed." : "Book not found.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
