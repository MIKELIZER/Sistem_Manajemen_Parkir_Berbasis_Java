package com.view;

import com.config.cConfig;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Login Petugas Parkir");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        // Komponen UI
        JPanel pUser = new JPanel();
        pUser.add(new JLabel("Username:"));
        txtUsername = new JTextField(15);
        pUser.add(txtUsername);

        JPanel pPass = new JPanel();
        pPass.add(new JLabel("Password:"));
        txtPassword = new JPasswordField(15);
        pPass.add(txtPassword);

        btnLogin = new JButton("LOGIN MASUK");
        btnLogin.setBackground(new Color(0, 120, 215));
        btnLogin.setForeground(Color.WHITE);

        add(new JLabel("MALL PARKING SYSTEM", SwingConstants.CENTER));
        add(pUser);
        add(pPass);
        add(btnLogin);

        btnLogin.addActionListener(e -> prosesLogin());
    }

    private void prosesLogin() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        try (Connection conn = cConfig.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM petugas WHERE username=? AND password=?")) {

            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int idUser = rs.getInt("id_user");
                String nama = rs.getString("username");
                String role = rs.getString("role");
                // Buat Log Shift Baru
                PreparedStatement psLog = conn.prepareStatement(
                        "INSERT INTO log_shift (id_user, waktu_login) VALUES (?, NOW())",
                        Statement.RETURN_GENERATED_KEYS);
                psLog.setInt(1, idUser);
                psLog.executeUpdate();

                ResultSet rsLog = psLog.getGeneratedKeys();
                int idLog = 0;
                if (rsLog.next()) {
                    idLog = rsLog.getInt(1);
                }

                // --- LOGIKA CEK ROLE ---
                // Cek apakah role-nya "admin"
                if ("admin".equalsIgnoreCase(role)) {
                    new AdminFrame(nama).setVisible(true); // Buka Panel Admin
                } else {
                    new MainFrame(idUser, idLog, nama).setVisible(true); // Buka Kasir Biasa
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username/Password Salah!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error Database: " + ex.getMessage());
        }
    }
}