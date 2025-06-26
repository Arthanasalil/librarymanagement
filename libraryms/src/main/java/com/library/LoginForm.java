package com.library;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;

    public LoginForm() {
        setTitle("Login");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 5, 5));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Role:"));
        roleCombo = new JComboBox<>(new String[]{"user", "admin"});
        add(roleCombo);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> login());
        add(loginBtn);

        JButton signupBtn = new JButton("Signup");
        signupBtn.addActionListener(e -> {
            dispose();
            new SignupForm();  // See SignupForm.java below.
        });
        add(signupBtn);

        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = roleCombo.getSelectedItem().toString();

        if (UserDAO.validateLogin(username, password, role)) {
            dispose();
            if ("admin".equals(role)) {
                new AdminDashboard();
            } else {
                new UserDashboard(username);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    }
}
