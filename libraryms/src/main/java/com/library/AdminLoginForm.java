package com.library;

import javax.swing.*;
import java.awt.event.*;

public class AdminLoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public AdminLoginForm() {
        setTitle("Admin Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 30, 80, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(120, 30, 120, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 70, 80, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 70, 120, 25);
        add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(90, 110, 100, 25);
        add(loginBtn);

        loginBtn.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (UserDAO.validateLogin(username, password, "admin")) {
            dispose();
            new AdminDashboard();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid admin credentials!");
        }
    }
}
