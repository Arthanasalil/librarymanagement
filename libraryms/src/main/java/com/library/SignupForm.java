package com.library;

import javax.swing.*;
import java.awt.*;

public class SignupForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public SignupForm() {
        setTitle("Signup");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 5, 5));

        add(new JLabel("Choose Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Choose Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JButton signupBtn = new JButton("Signup");
        signupBtn.addActionListener(e -> signup());
        add(signupBtn);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            new LoginForm();
            dispose();
        });
        add(backBtn);

        setVisible(true);
    }

    private void signup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        UserDAO.createUser(username, password);
        JOptionPane.showMessageDialog(this, "Signup successful! Please log in.");
        new LoginForm();
        dispose();
    }
}
