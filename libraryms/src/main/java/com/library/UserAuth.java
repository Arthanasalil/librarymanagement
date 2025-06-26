package com.library;

import javax.swing.*;
import java.sql.*;

public class UserAuth {
    public UserAuth() {
        String[] options = {"Login", "Signup"};
        int choice = JOptionPane.showOptionDialog(null, "Choose an option", "Login/Signup",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) login();
        else signup();
    }

    private void login() {
        String username = JOptionPane.showInputDialog("Username:");
        String password = JOptionPane.showInputDialog("Password:");

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT role FROM users WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                if (role.equals("admin")) new AdminPanel(username);
                else new LibrarySystem(username);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials");
                new UserAuth();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signup() {
        String username = JOptionPane.showInputDialog("Choose a username:");
        String password = JOptionPane.showInputDialog("Choose a password:");

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO users(username, password, role) VALUES (?, ?, 'user')");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Signup successful! Please log in.");
            login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}