package com.library;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private JTable bookTable;
    private JTable userTable;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Create a tabbed pane with two tabs.
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", bookPanel());
        add(tabs, BorderLayout.CENTER);

        // Bottom panel with Back button
        JPanel bottomPanel = new JPanel();
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            new LoginForm(); // Navigate back to the login form
            this.dispose();  // Close AdminDashboard
        });
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel bookPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JButton addBtn = new JButton("Add Book");
        JButton delBtn = new JButton("Delete Book");

        JPanel topPanel = new JPanel();
        topPanel.add(addBtn);
        topPanel.add(delBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        // This table now shows all books with their borrowed information.
        bookTable = new JTable(BookDAO.getBooksTableModel());
        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        addBtn.addActionListener(e -> BookDAO.addBookPrompt(this));
        delBtn.addActionListener(e -> BookDAO.deleteSelectedBook(bookTable, this));

        return panel;
    }

    
}
