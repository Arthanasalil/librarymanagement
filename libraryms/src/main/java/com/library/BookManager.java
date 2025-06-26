package com.library;

import javax.swing.*;
import java.awt.*;

public class BookManager extends JFrame {
    private JTable table;
    private JScrollPane scrollPane;

    public BookManager() {
        setTitle("Admin Dashboard - Book Manager");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        table = new JTable();
        loadData();

        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton addBtn = new JButton("Add Book");
        addBtn.addActionListener(e -> {
            BookDAO.addBookPrompt(this);
            loadData();
        });

        JButton deleteBtn = new JButton("Delete Book");
        deleteBtn.addActionListener(e -> {
            BookDAO.deleteSelectedBook(table, this);
            loadData();
        });

        JButton viewUsers = new JButton("View Borrowed Status");
        viewUsers.addActionListener(e -> {
            JTable statusTable = new JTable(UserDAO.getUserBookStatus());
            JOptionPane.showMessageDialog(this, new JScrollPane(statusTable), "User Borrow Status", JOptionPane.INFORMATION_MESSAGE);
        });

        buttons.add(addBtn);
        buttons.add(deleteBtn);
        buttons.add(viewUsers);

        add(buttons, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void loadData() {
        table.setModel(BookDAO.getBooksTableModel());
    }
}
