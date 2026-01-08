package com.view;

import com.config.cConfig;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {

    private final int currentUserId;
    private final int currentLogId;
    private final String currentUsername;

    // Komponen Tab Pembayaran
    private JTextField txtCariTiket;
    private JCheckBox chkTiketHilang;
    private JLabel lblPlat, lblDurasi, lblTotal;
    private JButton btnBayar;

    // Komponen Tab Laporan (BARU)
    private JTable tblLaporan;
    private JLabel lblTotalPendapatanShift;
    private DefaultTableModel tableModelLaporan;

    // Variabel Hitungan
    private int selectedIdTransaksi = 0;
    private double calculatedTotal = 0;
    private double nilaiDenda = 0;
    private long calculatedDuration = 0;
    private String jamMasukUntukStruk = "";

    public MainFrame(int userId, int logId, String username) {
        this.currentUserId = userId;
        this.currentLogId = logId;
        this.currentUsername = username;

        setTitle("Dashboard Parkir Profesional - Petugas: " + username);
        setSize(950, 650); // Ukuran sedikit diperlebar agar tabel muat
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Font lebih modern

        // Menambahkan Tab
        tabs.addTab("  🚗 MASUK (Check-In)  ", createTabMasuk());
        tabs.addTab("  💸 KELUAR (Pembayaran)  ", createTabKeluar());
        tabs.addTab("  📊 LAPORAN SHIFT  ", createTabLaporan()); // TAB BARU!
        tabs.addTab("  🚪 LOGOUT  ", createTabLogout());

        add(tabs);
    }

    // =======================================================
    // TAB 1: KENDARAAN MASUK
    // =======================================================
    private JPanel createTabMasuk() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtPlat = new JTextField(15);
        txtPlat.setFont(new Font("Monospaced", Font.BOLD, 18)); // Font plat nomor besar

        String[] jenis = {"1 - Mobil", "2 - Motor"};
        JComboBox<String> cmbJenis = new JComboBox<>(jenis);

        JButton btnSimpan = new JButton("CETAK TIKET (ENTER)");
        btnSimpan.setBackground(new Color(40, 167, 69));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Arial", Font.BOLD, 14));

        JButton btnLihat = new JButton("Lihat Kendaraan Menginap");

        // Layouting
        g.gridx=0; g.gridy=0; p.add(new JLabel("No Polisi:"), g);
        g.gridx=1; p.add(txtPlat, g);

        g.gridx=0; g.gridy=1; p.add(new JLabel("Jenis:"), g);
        g.gridx=1; p.add(cmbJenis, g);

        g.gridx=0; g.gridy=2; g.gridwidth=2;
        g.ipady=20; // Tombol lebih tinggi
        p.add(btnSimpan, g);

        g.gridy=3; g.ipady=0;
        p.add(btnLihat, g);

        // Action: Simpan Data
        btnSimpan.addActionListener(e -> {
            if(txtPlat.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Plat Nomor wajib diisi!");
                return;
            }
            simpanTransaksiMasuk(txtPlat.getText(), cmbJenis.getSelectedIndex() + 1);
            txtPlat.setText(""); // Reset
        });



        btnLihat.addActionListener(e -> showParkedList());
        return p;
    }

    private void simpanTransaksiMasuk(String plat, int idJenis) {
        try (Connection conn = cConfig.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO transaksi (no_polisi, id_jenis, id_petugas_masuk, waktu_masuk, status) VALUES (?,?,?,NOW(),'PARKIR')",
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, plat.toUpperCase());
            ps.setInt(2, idJenis);
            ps.setInt(3, currentUserId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()) {
                JOptionPane.showMessageDialog(this, "✅ Tiket Berhasil!\nID Tiket: " + rs.getInt(1));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void showParkedList() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID Tiket", "Plat", "Waktu Masuk"}, 0);
        try (Connection conn = cConfig.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM transaksi WHERE status='PARKIR' ORDER BY waktu_masuk DESC")) {
            while(rs.next()) {
                model.addRow(new Object[]{rs.getInt("id_transaksi"), rs.getString("no_polisi"), rs.getString("waktu_masuk")});
            }
        } catch (Exception ex) {}
        JOptionPane.showMessageDialog(this, new JScrollPane(new JTable(model)), "Kendaraan Belum Keluar", JOptionPane.PLAIN_MESSAGE);
    }

    // =======================================================
    // TAB 2: PEMBAYARAN (KELUAR)
    // =======================================================
    private JPanel createTabKeluar() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel Atas
        JPanel top = new JPanel(new FlowLayout());
        txtCariTiket = new JTextField(10);
        txtCariTiket.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnCek = new JButton("🔍 CEK TARIF");
        chkTiketHilang = new JCheckBox("Tiket Hilang?");
        chkTiketHilang.setForeground(Color.RED);
        chkTiketHilang.setFont(new Font("Arial", Font.BOLD, 12));

        top.add(new JLabel("ID Tiket:"));
        top.add(txtCariTiket);
        top.add(chkTiketHilang);
        top.add(btnCek);
        p.add(top, BorderLayout.NORTH);

        // Panel Tengah (Info)
        JPanel center = new JPanel(new GridLayout(3, 1));
        lblPlat = new JLabel("Plat Nomor: -", SwingConstants.CENTER);
        lblPlat.setFont(new Font("Arial", Font.PLAIN, 18));

        lblDurasi = new JLabel("Durasi: -", SwingConstants.CENTER);
        lblDurasi.setFont(new Font("Arial", Font.PLAIN, 18));

        lblTotal = new JLabel("Rp 0", SwingConstants.CENTER);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 48)); // Harga Sangat Besar
        lblTotal.setForeground(new Color(220, 53, 69));

        center.add(lblPlat); center.add(lblDurasi); center.add(lblTotal);
        p.add(center, BorderLayout.CENTER);

        // Panel Bawah
        btnBayar = new JButton("💰 BAYAR & BUKA PALANG");
        btnBayar.setEnabled(false);
        btnBayar.setPreferredSize(new Dimension(200, 60));
        btnBayar.setBackground(new Color(23, 162, 184));
        btnBayar.setForeground(Color.WHITE);
        btnBayar.setFont(new Font("Arial", Font.BOLD, 16));
        p.add(btnBayar, BorderLayout.SOUTH);

        btnCek.addActionListener(e -> hitungTarif());
        btnBayar.addActionListener(e -> prosesBayar());

        return p;
    }

    private void hitungTarif() {
        try (Connection conn = cConfig.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM transaksi WHERE id_transaksi=?");
            try {
                ps.setInt(1, Integer.parseInt(txtCariTiket.getText()));
            } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "ID Tiket harus angka!"); return; }

            ResultSet rs = ps.executeQuery();
            if (rs.next() && "PARKIR".equals(rs.getString("status"))) {
                LocalDateTime masuk = rs.getTimestamp("waktu_masuk").toLocalDateTime();
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
                jamMasukUntukStruk = masuk.format(fmt); // Simpan ke variabel global

                long jam = Duration.between(masuk, LocalDateTime.now()).toHours();
                if (Duration.between(masuk, LocalDateTime.now()).toMinutesPart() > 0) jam++;
                if (jam == 0) jam = 1;

                PreparedStatement psTarif = conn.prepareStatement("SELECT * FROM tarif WHERE id_jenis=?");
                psTarif.setInt(1, rs.getInt("id_jenis"));
                ResultSet rsTarif = psTarif.executeQuery();

                if (rsTarif.next()) {
                    double awal = rsTarif.getDouble("tarif_awal");
                    double perJam = rsTarif.getDouble("tarif_per_jam");
                    double dendaDb = rsTarif.getDouble("denda_tiket_hilang");

                    double total = awal + ((jam - 1) * perJam);

                    nilaiDenda = 0;
                    String infoTam = "";
                    if (chkTiketHilang.isSelected()) {
                        nilaiDenda = dendaDb;
                        total += nilaiDenda;
                        infoTam = " (Termasuk Denda)";
                    }

                    selectedIdTransaksi = rs.getInt("id_transaksi");
                    calculatedDuration = jam;
                    calculatedTotal = total;

                    lblPlat.setText("Plat: " + rs.getString("no_polisi"));
                    lblDurasi.setText("Durasi: " + jam + " Jam" + infoTam);
                    lblTotal.setText("Rp " + String.format("%,.0f", total));
                    btnBayar.setEnabled(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tiket tidak ditemukan / sudah keluar.");
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void prosesBayar() {
        try (Connection conn = cConfig.connect()) {
            // 1. Tentukan Status & Query Update
            String statusAkhir = chkTiketHilang.isSelected() ? "TIKET_HILANG" : "SELESAI";
            String sql = "UPDATE transaksi SET waktu_keluar=NOW(), durasi_jam=?, total_bayar=?, denda=?, id_petugas_keluar=?, status=? WHERE id_transaksi=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, calculatedDuration);
            ps.setDouble(2, calculatedTotal);
            ps.setDouble(3, nilaiDenda);
            ps.setInt(4, currentUserId);
            ps.setString(5, statusAkhir);
            ps.setInt(6, selectedIdTransaksi);

            // 2. Eksekusi ke Database
            int hasil = ps.executeUpdate();

            if (hasil > 0) {
                // === A. TAMPILKAN SUKSES ===
                JOptionPane.showMessageDialog(this, "✅ Pembayaran Sukses! Palang Dibuka.");
                // === FITUR CETAK STRUK ===
                int tanyaPrint = JOptionPane.showConfirmDialog(this, "Cetak Struk Pembayaran?", "Cetak", JOptionPane.YES_NO_OPTION);
                if (tanyaPrint == JOptionPane.YES_OPTION) {
                    try {
                        String strukId = String.valueOf(selectedIdTransaksi);
                        // Bersihkan teks label biar cuma ambil nomor platnya saja
                        String strukPlat = lblPlat.getText().replace("Plat Nomor: ", "").replace("Plat: ", "").trim();
                        String strukDurasi = String.valueOf(calculatedDuration);
                        String strukTotal = lblTotal.getText();
                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
                        String strukKeluar = LocalDateTime.now().format(fmt);
                        String strukMasuk = jamMasukUntukStruk;

                        // Panggil Class StrukPrinter
                        StrukPrinter printer = new StrukPrinter(
                                strukId,
                                strukPlat,
                                strukMasuk,
                                strukKeluar,
                                strukDurasi,
                                strukTotal,
                                currentUsername
                        );
                        printer.printStruk(); // Memunculkan Dialog Print Windows

                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Gagal Mencetak: " + e.getMessage());
                    }
                }
                // ============================================================

                // === C. REFRESH DASHBOARD & TABEL ===
                refreshLaporan();
                refreshStatistikHeader();

                // === D. RESET TAMPILAN ===
                btnBayar.setEnabled(false);
                txtCariTiket.setText("");
                lblTotal.setText("Rp 0");
                lblPlat.setText("-");
                lblDurasi.setText("Durasi: -");
                chkTiketHilang.setSelected(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // =======================================================
    // TAB 3: LAPORAN SHIFT & DASHBOARD STATISTIK
    // =======================================================
    private JPanel createTabLaporan() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // -----------------------------------------------------------
        // BAGIAN 1: HEADER (STATISTIK + TOMBOL) - [MODIFIKASI DISINI]
        // -----------------------------------------------------------
        JPanel pAtas = new JPanel(new BorderLayout(0, 15)); // Container atas

        // A. Pasang Panel Statistik (3 Kartu Warna)
        // Pastikan method createPanelStatistik() sudah Anda copy di bawah file
        pAtas.add(createPanelStatistik(), BorderLayout.CENTER);

        // B. Toolbar (Judul & Tombol) ditaruh di bawah kartu
        JPanel pToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblJudul = new JLabel("Riwayat Transaksi (" + currentUsername + ")");
        lblJudul.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnRefresh = new JButton("🔄 Refresh Data");
        JButton btnExport = new JButton("📂 Export CSV");

        pToolbar.add(lblJudul);
        pToolbar.add(btnRefresh);
        pToolbar.add(btnExport);

        // Gabungkan Toolbar ke bagian bawah panel statistik
        pAtas.add(pToolbar, BorderLayout.SOUTH);

        // Tempelkan seluruh bagian atas ke Layout Utama
        p.add(pAtas, BorderLayout.NORTH);

        // -----------------------------------------------------------
        // BAGIAN 2: TABEL (SAMA SEPERTI KODE LAMA)
        // -----------------------------------------------------------
        String[] kolom = {"ID Tiket", "Plat Nomor", "Jenis", "Masuk", "Keluar", "Durasi", "Total Bayar", "Status"};
        tableModelLaporan = new DefaultTableModel(kolom, 0);
        tblLaporan = new JTable(tableModelLaporan);

        // Styling Tabel
        tblLaporan.setRowHeight(25);
        tblLaporan.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblLaporan.getTableHeader().setBackground(new Color(230, 230, 230));

        // Rata Tengah Kolom Tertentu
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblLaporan.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        tblLaporan.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Durasi

        p.add(new JScrollPane(tblLaporan), BorderLayout.CENTER);

        // -----------------------------------------------------------
        // BAGIAN 3: FOOTER (SAMA SEPERTI KODE LAMA)
        // -----------------------------------------------------------
        JPanel pFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pFooter.setBackground(new Color(240, 240, 240));
        lblTotalPendapatanShift = new JLabel("Total Pendapatan: Rp 0");
        lblTotalPendapatanShift.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalPendapatanShift.setForeground(new Color(40, 167, 69));

        pFooter.add(lblTotalPendapatanShift);
        p.add(pFooter, BorderLayout.SOUTH);

        // -----------------------------------------------------------
        // BAGIAN 4: EVENTS (UPDATE PENTING)
        // -----------------------------------------------------------
        btnRefresh.addActionListener(e -> {
            refreshLaporan();         // Update isi tabel
            refreshStatistikHeader(); // Update angka di kartu warna-warni (Method Baru)
        });

        btnExport.addActionListener(e -> exportLaporanKeCSV());

        // Load data pertama kali saat aplikasi dibuka
        refreshLaporan();
        refreshStatistikHeader(); // Panggil agar kartu langsung muncul angkanya

        return p;
    }

    private void refreshLaporan() {
        // Hapus data lama di tabel
        tableModelLaporan.setRowCount(0);
        double totalUang = 0;

        // Query: Ambil transaksi yang diselesaikan oleh user ini di shift ini
        String sql = "SELECT t.*, j.jenis_kendaraan FROM transaksi t " +
                "JOIN jenis_kendaraan j ON t.id_jenis = j.id_jenis " +
                "WHERE t.id_petugas_keluar = ? " +
                "AND t.status IN ('SELESAI', 'TIKET_HILANG') " +
                "AND t.waktu_keluar >= (SELECT waktu_login FROM log_shift WHERE id_log = ?) " +
                "ORDER BY t.waktu_keluar DESC";

        try (Connection conn = cConfig.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, currentUserId);
            ps.setInt(2, currentLogId);
            ResultSet rs = ps.executeQuery();

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");

            while (rs.next()) {
                double bayar = rs.getDouble("total_bayar");
                totalUang += bayar;

                LocalDateTime masuk = rs.getTimestamp("waktu_masuk").toLocalDateTime();
                LocalDateTime keluar = rs.getTimestamp("waktu_keluar").toLocalDateTime();

                tableModelLaporan.addRow(new Object[]{
                        rs.getInt("id_transaksi"),
                        rs.getString("no_polisi"),
                        rs.getString("jenis_kendaraan"),
                        masuk.format(fmt),
                        keluar.format(fmt),
                        rs.getInt("durasi_jam") + " Jam",
                        "Rp " + String.format("%,.0f", bayar),
                        rs.getString("status")
                });
            }

            // Update Label Total
            lblTotalPendapatanShift.setText("Total Pendapatan Shift Ini: Rp " + String.format("%,.0f", totalUang));

        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // Fitur Bonus: Export ke CSV
    // Jangan lupa import file di paling atas jika belum ada:
    // import java.io.File;

    // Pastikan import ini ada di paling atas file:
    // import java.io.File;
    // import java.io.FileWriter;
    // import java.io.IOException;
    // import java.awt.Desktop;

    private void exportLaporanKeCSV() {
        try {
            // 1. Ambil jalur ke folder OneDrive kamu sesuai screenshot
            String userHome = System.getProperty("user.home");
            String folderPath = userHome + "/OneDrive/Documents/belajar/";

            File folder = new File(folderPath);

            // Jaga-jaga: Jika folder OneDrive tidak ketemu, coba Documents biasa
            if (!folder.exists()) {
                folderPath = userHome + "/Documents/belajar/";
                folder = new File(folderPath);
            }

            // Buat folder 'belajar' jika belum ada
            if (!folder.exists()) {
                boolean dibuat = folder.mkdirs();
                if(dibuat) System.out.println("Folder belajar berhasil dibuat.");
            }

            // 2. Siapkan nama file
            String fileName = folderPath + "Laporan_Shift_" + currentUsername + ".csv";

            // 3. Tulis data ke file
            FileWriter fw = new FileWriter(fileName);
            fw.write("ID,Plat,Jenis,Total,Status\n"); // Header

            for(int i = 0; i < tableModelLaporan.getRowCount(); i++) {
                fw.write(tableModelLaporan.getValueAt(i, 0) + ",");
                fw.write(tableModelLaporan.getValueAt(i, 1) + ",");
                fw.write(tableModelLaporan.getValueAt(i, 2) + ",");
                // Bersihkan format Rupiah sebelum simpan
                fw.write(tableModelLaporan.getValueAt(i, 6).toString().replace("Rp ", "").replace(",", "") + ",");
                fw.write(tableModelLaporan.getValueAt(i, 7) + "\n");
            }
            fw.close();

            // 4. Beri notifikasi & Buka Folder Otomatis
            int tanya = JOptionPane.showConfirmDialog(this,
                    "✅ Data Berhasil Disimpan!\nLokasi: " + fileName + "\n\nBuka folder sekarang?",
                    "Sukses Export", JOptionPane.YES_NO_OPTION);

            if (tanya == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(folder); // Ini akan membuka File Explorer otomatis
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal export: " + ex.getMessage());
        }
    }
    // =======================================================
    // TAB 4: LOGOUT
    // =======================================================
    private JPanel createTabLogout() {
        JPanel p = new JPanel(new GridBagLayout());
        JButton btn = new JButton("⛔ TUTUP SHIFT & LOGOUT");
        btn.setBackground(Color.RED);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(250, 60));

        p.add(new JLabel("User Aktif: " + currentUsername + "  "), new GridBagConstraints());
        p.add(btn, new GridBagConstraints());

        btn.addActionListener(e -> performLogout());
        return p;
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin tutup shift?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = cConfig.connect()) {
                // Gunakan query rekap yang sama dengan tabel laporan biar konsisten
                refreshLaporan();

                // Ambil nilai dari label saja biar cepat (karena sudah dihitung di refreshLaporan)
                String textTotal = lblTotalPendapatanShift.getText().replaceAll("[^0-9]", "");
                double totalDuit = textTotal.isEmpty() ? 0 : Double.parseDouble(textTotal);
                int totalQty = tableModelLaporan.getRowCount();

                PreparedStatement psUp = conn.prepareStatement(
                        "UPDATE log_shift SET waktu_logout=NOW(), total_transaksi=?, total_pendapatan=? WHERE id_log=?");
                psUp.setInt(1, totalQty);
                psUp.setDouble(2, totalDuit);
                psUp.setInt(3, currentLogId);
                psUp.executeUpdate();

                JOptionPane.showMessageDialog(this, "Shift Berakhir.\nTotal: " + totalQty + " Kendaraan\nPendapatan: Rp " + String.format("%,.0f", totalDuit));
                new LoginFrame().setVisible(true);
                this.dispose();

            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
    // Method untuk membuat panel kartu statistik
    private JPanel createInfoCard(String judul, String nilai, Color warna) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(warna);
        card.setPreferredSize(new Dimension(200, 100));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setForeground(Color.WHITE);
        lblJudul.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblNilai = new JLabel(nilai);
        lblNilai.setForeground(Color.WHITE);
        lblNilai.setFont(new Font("Arial", Font.BOLD, 28));
        lblNilai.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(lblJudul, BorderLayout.NORTH);
        card.add(lblNilai, BorderLayout.CENTER);

        return card;
    }

    // Panel penampung kartu
    private JPanel panelStatistik;
    private JLabel lblStatMasuk, lblStatParkir, lblStatDuit;

    private JPanel createPanelStatistik() {
        panelStatistik = new JPanel(new GridLayout(1, 3, 20, 0)); // 3 Kolom
        panelStatistik.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Jarak bawah

        // Placeholder awal
        panelStatistik.add(createInfoCard("Kendaraan Yang Masuk Hari Ini (Hari Ini)", "0", new Color(23, 162, 184))); // Biru
        panelStatistik.add(createInfoCard("Sedang Parkir (Aktif)", "0", new Color(255, 193, 7))); // Kuning
        panelStatistik.add(createInfoCard("Pendapatan (Hari Ini)", "Rp 0", new Color(40, 167, 69))); // Hijau

        return panelStatistik;
    }

    // Logic Mengambil Data Realtime dari Database
    private void refreshStatistikHeader() {
        // Hapus kartu lama
        panelStatistik.removeAll();

        int masukHariIni = 0;
        int sedangParkir = 0;
        double duitHariIni = 0;

        try (Connection conn = cConfig.connect()) {
            // 1. Hitung Masuk Hari Ini
            Statement st1 = conn.createStatement();
            ResultSet rs1 = st1.executeQuery("SELECT COUNT(*) FROM transaksi WHERE DATE(waktu_masuk) = CURDATE()");
            if(rs1.next()) masukHariIni = rs1.getInt(1);

            // 2. Hitung Sedang Parkir (Status = PARKIR)
            Statement st2 = conn.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT COUNT(*) FROM transaksi WHERE status = 'PARKIR'");
            if(rs2.next()) sedangParkir = rs2.getInt(1);

            // 3. Hitung Duit Hari Ini (Status SELESAI/HILANG)
            Statement st3 = conn.createStatement();
            ResultSet rs3 = st3.executeQuery("SELECT SUM(total_bayar) FROM transaksi WHERE status != 'PARKIR' AND DATE(waktu_keluar) = CURDATE()");
            if(rs3.next()) duitHariIni = rs3.getDouble(1);

        } catch (Exception e) { e.printStackTrace(); }

        // Tambahkan kartu baru dengan data update
        panelStatistik.add(createInfoCard("Masuk Hari Ini", String.valueOf(masukHariIni), new Color(23, 162, 184)));
        panelStatistik.add(createInfoCard("Sedang Parkir", String.valueOf(sedangParkir), new Color(255, 165, 0)));
        panelStatistik.add(createInfoCard("Omzet Hari Ini", "Rp " + String.format("%,.0f", duitHariIni), new Color(40, 167, 69)));

        panelStatistik.revalidate();
        panelStatistik.repaint();
    }
}