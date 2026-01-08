package com.view;

import java.awt.*;
import java.awt.print.*;

public class StrukPrinter implements Printable {

    // Data yang mau dicetak kita simpan di variabel ini
    private String noTiket;
    private String platNomor;
    private String jamMasuk;
    private String jamKeluar;
    private String durasi;
    private String totalBayar;
    private String petugas;

    // Constructor: Untuk menerima data dari MainFrame
    public StrukPrinter(String noTiket, String plat, String masuk, String keluar, String durasi, String total, String petugas) {
        this.noTiket = noTiket;
        this.platNomor = plat;
        this.jamMasuk = masuk;
        this.jamKeluar = keluar;
        this.durasi = durasi;
        this.totalBayar = total;
        this.petugas = petugas;
    }

    // Method Utama untuk memanggil print dialog
    public void printStruk() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);

        // Menampilkan Dialog Print bawaan Windows
        boolean doPrint = job.printDialog();

        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }

    // DISINI KITA MENGGAMBAR TAMPILAN KERTASNYA
    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) {
            return NO_SUCH_PAGE;
        }

        // Konversi Graphics jadi Graphics2D biar lebih powerful
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        // Setting Font (Monospaced biar kayak mesin kasir)
        g.setFont(new Font("Monospaced", Font.BOLD, 10));

        int y = 15; // Posisi Y (baris) awal
        int x = 10; // Margin kiri
        int spasi = 12; // Jarak antar baris

        // --- MULAI MENGGAMBAR TEKS ---
        g.drawString("====== MALL PARKING ======", x, y); y+=spasi;
        g.drawString("   Jl. Merdeka No. 45     ", x, y); y+=spasi;
        g.drawString("==========================", x, y); y+=spasi;
        y+=5; // Jarak dikit

        g.drawString("Tiket   : " + noTiket, x, y); y+=spasi;
        g.drawString("Petugas : " + petugas, x, y); y+=spasi;
        g.drawString("--------------------------", x, y); y+=spasi;

        g.setFont(new Font("Monospaced", Font.BOLD, 12)); // Font agak besar buat Plat
        g.drawString("PLAT    : " + platNomor, x, y); y+=spasi;
        g.setFont(new Font("Monospaced", Font.PLAIN, 10)); // Balik normal

        g.drawString("Masuk   : " + jamMasuk, x, y); y+=spasi;
        g.drawString("Keluar  : " + jamKeluar, x, y); y+=spasi;
        g.drawString("Durasi  : " + durasi + " Jam", x, y); y+=spasi;
        g.drawString("--------------------------", x, y); y+=spasi;

        g.setFont(new Font("Monospaced", Font.BOLD, 14)); // Font Besar buat Harga
        g.drawString("TOTAL   : " + totalBayar, x, y); y+=spasi+5;

        g.setFont(new Font("Monospaced", Font.ITALIC, 8));
        g.drawString("Terima Kasih & Hati-hati", x, y); y+=spasi;
        g.drawString("   Simpan Struk Ini     ", x, y);

        return PAGE_EXISTS;
    }
}