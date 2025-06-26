package com.library;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {
    private JTable availableBooksTable;
    private JTable borrowedBooksTable;
    private String username;

    public UserDashboard(String username) {
        this.username = username;
        setTitle("User Dashboard - " + username);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Welcome, " + username + " | Library System");
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        // Center: Panels for Available and Borrowed books
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // ========== AVAILABLE BOOKS ==========
        JPanel availablePanel = new JPanel(new BorderLayout());
        JLabel availableLabel = new JLabel("Available Books");
        availableLabel.setHorizontalAlignment(SwingConstants.CENTER);
        availableLabel.setFont(new Font("Arial", Font.BOLD, 16));
        availablePanel.add(availableLabel, BorderLayout.NORTH);

        availableBooksTable = new JTable(BookDAO.getAvailableBooksTableModel());
        availablePanel.add(new JScrollPane(availableBooksTable), BorderLayout.CENTER);

        JButton borrowBtn = new JButton("Borrow Selected Book");
        borrowBtn.addActionListener(e -> {
            BookDAO.borrowBook(availableBooksTable, username);
            refreshTables();
        });
        JPanel borrowBtnPanel = new JPanel();
        borrowBtnPanel.add(borrowBtn);
        availablePanel.add(borrowBtnPanel, BorderLayout.SOUTH);

        // ========== BORROWED BOOKS ==========
        JPanel borrowedPanel = new JPanel(new BorderLayout());
        JLabel borrowedLabel = new JLabel("Your Borrowed Books");
        borrowedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        borrowedLabel.setFont(new Font("Arial", Font.BOLD, 16));
        borrowedPanel.add(borrowedLabel, BorderLayout.NORTH);

        borrowedBooksTable = new JTable(BookDAO.getBorrowedBooksTableModel(username));
        borrowedPanel.add(new JScrollPane(borrowedBooksTable), BorderLayout.CENTER);

        JButton returnBtn = new JButton("Return Selected Book");
        returnBtn.addActionListener(e -> {
            BookDAO.returnBook(borrowedBooksTable, username);
            refreshTables();
        });
        JPanel returnBtnPanel = new JPanel();
        returnBtnPanel.add(returnBtn);
        borrowedPanel.add(returnBtnPanel, BorderLayout.SOUTH);

        // Add both panels
        centerPanel.add(availablePanel);
        centerPanel.add(borrowedPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Footer with back button
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            new LoginForm(); // Go back to login
            this.dispose();
        });
        JPanel footer = new JPanel();
        footer.add(backBtn);
        add(footer, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void refreshTables() {
        availableBooksTable.setModel(BookDAO.getAvailableBooksTableModel());
        borrowedBooksTable.setModel(BookDAO.getBorrowedBooksTableModel(username));
    }
}
