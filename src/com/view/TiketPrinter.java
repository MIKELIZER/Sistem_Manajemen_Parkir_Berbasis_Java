package com.view;

import java.awt.*;
import java.awt.print.*;

public class TiketPrinter implements Printable {

    private String idTiket;
    private String platNomor;
    private String jenisKendaraan;
    private String waktuMasuk;
    private String petugas;

    public TiketPrinter(String idTiket, String plat, String jenis, String waktu, String petugas) {
        this.idTiket = idTiket;
        this.platNomor = plat;
        this.jenisKendaraan = jenis;
        this.waktuMasuk = waktu;
        this.petugas = petugas;
    }

    public void printTiket() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        // Hilangkan dialog print bawaan windows biar cepat (langsung print)
        // Kalau mau tetap ada dialog, hapus komen di bawah ini:
        // boolean doPrint = job.printDialog();
        // if (doPrint) { ... }

        try {
            job.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) return NO_SUCH_PAGE;

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        int y = 20;
        int x = 10;

        // --- HEADER ---
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString("    GRAND MALL PARKIR    ", x, y); y += 15;
        g.setFont(new Font("Monospaced", Font.PLAIN, 8));
        g.drawString("   Jl. Jendral Sudirman No.1   ", x, y); y += 20;
        g.drawString("--------------------------------", x, y); y += 15;

        // --- ID TIKET (Dibuat Besar & Menonjol) ---
        g.setFont(new Font("SansSerif", Font.BOLD, 24)); // Font Besar
        g.drawString("TIKET: " + idTiket, x, y + 5); y += 35;

        // --- DETAIL ---
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString("PLAT  : " + platNomor, x, y); y += 15;
        g.drawString("JENIS : " + jenisKendaraan, x, y); y += 15;
        g.drawString("MASUK : " + waktuMasuk, x, y); y += 15;
        g.drawString("PETUGAS: " + petugas, x, y); y += 20;

        // --- GARIS PEMBATAS ---
        g.drawString("--------------------------------", x, y); y += 15;

        // --- FOOTER / DISCLAIMER (Penting buat Mall) ---
        g.setFont(new Font("Monospaced", Font.ITALIC, 7));
        g.drawString("* Jangan tinggalkan tiket & barang berharga", x, y); y += 10;
        g.drawString("* Hilang tiket denda Rp 50.000", x, y); y += 10;
        g.drawString("* Kerusakan/Kehilangan bukan tanggung jawab", x, y); y += 10;
        g.drawString("  pengelola parkir.", x, y);

        return PAGE_EXISTS;
    }
}