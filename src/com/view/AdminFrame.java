package com.view;

import com.config.cConfig;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminFrame extends JFrame {

    public AdminFrame(String username) {
        setTitle("ADMINISTRATOR PANEL - Mall Parking System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Header
        JLabel lblTitle = new JLabel("Selamat Datang, Admin " + username);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(lblTitle, BorderLayout.NORTH);

        // Tab Menu
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabs.addTab("👥 KELOLA PETUGAS", createTabPetugas());
        tabs.addTab("💰 ATUR TARIF", createTabTarif());
        tabs.addTab("🚪 LOGOUT", createTabLogout());

        add(tabs, BorderLayout.CENTER);
    }

    // ==========================================================
    // TAB 1: KELOLA PETUGAS (CRUD)
    // ==========================================================
    private JTextField txtUser, txtPass;
    private JComboBox<String> cmbRole; // Ganti nama jadi Role
    private JTable tblPetugas;
    private DefaultTableModel modelPetugas;

    private JPanel createTabPetugas() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Input
        JPanel pInput = new JPanel(new GridLayout(4, 2, 5, 5));
        txtUser = new JTextField();
        txtPass = new JTextField();

        //cmb buat pilih role
        cmbRole = new JComboBox<>(new String[]{"petugas", "admin"});

        JButton btnSimpan = new JButton("💾 SIMPAN USER BARU");
        JButton btnHapus = new JButton("🗑 HAPUS YANG DIPILIH");

        pInput.add(new JLabel("Username:")); pInput.add(txtUser);
        pInput.add(new JLabel("Password:")); pInput.add(txtPass);
        pInput.add(new JLabel("Role:"));     pInput.add(cmbRole);
        pInput.add(btnHapus); pInput.add(btnSimpan);

        p.add(pInput, BorderLayout.NORTH);

        // Tabel
        String[] kolom = {"ID", "Username", "Password", "Role"};
        modelPetugas = new DefaultTableModel(kolom, 0);
        tblPetugas = new JTable(modelPetugas);
        p.add(new JScrollPane(tblPetugas), BorderLayout.CENTER);

        // Load Data Awal
        refreshTablePetugas();

        // Event Listeners
        btnSimpan.addActionListener(e -> simpanPetugas());
        btnHapus.addActionListener(e -> hapusPetugas());

        return p;
    }

    private void refreshTablePetugas() {
        modelPetugas.setRowCount(0);
        try (Connection conn = cConfig.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM petugas")) {
            while (rs.next()) {
                modelPetugas.addRow(new Object[]{
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role") // Ambil kolom 'role'
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void simpanPetugas() {
        try (Connection conn = cConfig.connect();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO petugas (username, password, role) VALUES (?,?,?)")) {

            ps.setString(1, txtUser.getText());
            ps.setString(2, txtPass.getText());
            ps.setString(3, cmbRole.getSelectedItem().toString());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User Berhasil Ditambah!");
            refreshTablePetugas();
            txtUser.setText(""); txtPass.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal: " + e.getMessage());
        }
    }

    private void hapusPetugas() {
        int row = tblPetugas.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang mau dihapus dulu!");
            return;
        }

        String idUser = modelPetugas.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus user ini?", "Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = cConfig.connect();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM petugas WHERE id_user=?")) {
                ps.setString(1, idUser);
                ps.executeUpdate();
                refreshTablePetugas();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // ==========================================================
    // TAB 2: ATUR TARIF (SAMA SEPERTI SEBELUMNYA)
    // ==========================================================
    private JTable tblTarif;
    private DefaultTableModel modelTarif;

    private JPanel createTabTarif() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("<html>*Klik baris tabel, edit nilainya di tabel langsung, lalu tekan ENTER.<br>Kemudian klik tombol 'SIMPAN PERUBAHAN' di bawah.</html>");
        lblInfo.setForeground(Color.BLUE);
        p.add(lblInfo, BorderLayout.NORTH);

        String[] kolom = {"ID Jenis", "Jenis Kendaraan", "Tarif Awal (Rp)", "Per Jam (Rp)", "Denda Hilang (Rp)"};
        modelTarif = new DefaultTableModel(kolom, 0);
        tblTarif = new JTable(modelTarif);
        p.add(new JScrollPane(tblTarif), BorderLayout.CENTER);

        JButton btnUpdate = new JButton("💾 SIMPAN PERUBAHAN HARGA");
        btnUpdate.setBackground(new Color(255, 193, 7));
        p.add(btnUpdate, BorderLayout.SOUTH);

        refreshTableTarif();
        btnUpdate.addActionListener(e -> updateTarifDatabase());

        return p;
    }

    private void refreshTableTarif() {
        modelTarif.setRowCount(0);
        try (Connection conn = cConfig.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT t.*, j.jenis_kendaraan FROM tarif t JOIN jenis_kendaraan j ON t.id_jenis = j.id_jenis")) {
            while (rs.next()) {
                modelTarif.addRow(new Object[]{
                        rs.getInt("id_jenis"),
                        rs.getString("jenis_kendaraan"),
                        rs.getDouble("tarif_awal"),
                        rs.getDouble("tarif_per_jam"),
                        rs.getDouble("denda_tiket_hilang")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateTarifDatabase() {
        try (Connection conn = cConfig.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE tarif SET tarif_awal=?, tarif_per_jam=?, denda_tiket_hilang=? WHERE id_jenis=?");

            for (int i = 0; i < modelTarif.getRowCount(); i++) {
                double awal = Double.parseDouble(modelTarif.getValueAt(i, 2).toString());
                double perjam = Double.parseDouble(modelTarif.getValueAt(i, 3).toString());
                double denda = Double.parseDouble(modelTarif.getValueAt(i, 4).toString());
                int id = Integer.parseInt(modelTarif.getValueAt(i, 0).toString());

                ps.setDouble(1, awal);
                ps.setDouble(2, perjam);
                ps.setDouble(3, denda);
                ps.setInt(4, id);
                ps.addBatch();
            }
            ps.executeBatch();
            JOptionPane.showMessageDialog(this, "Harga Berhasil Diupdate!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal Update: " + e.getMessage());
        }
    }

    // ==========================================================
    // TAB LOGOUT
    // ==========================================================
    private JPanel createTabLogout() {
        JPanel p = new JPanel(new GridBagLayout());
        JButton btn = new JButton("LOGOUT ADMIN");
        btn.setBackground(Color.RED);
        btn.setForeground(Color.WHITE);
        btn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        p.add(btn);
        return p;
    }
}