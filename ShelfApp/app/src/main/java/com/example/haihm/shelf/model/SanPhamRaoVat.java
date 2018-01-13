package com.example.haihm.shelf.model;

import java.util.List;

/**
 * Created by Trần_Tân on 08/01/2018.
 */

public class SanPhamRaoVat {
    public String idNB;
    public String tenSP;
    public List<String> anhSP ;
    public double giaSP;
    public String motaSP;
    public String loaiSP;
    public String hoTenNB;
    public String sdtNB;
    public String diaGD;

    public SanPhamRaoVat(String idNB, String tenSP, List<String> anhSP, double giaSP,
                         String motaSP, String loaiSP, String hoTenNB, String sdtNB, String diaGD) {
        this.idNB = idNB;
        this.tenSP = tenSP;
        this.anhSP = anhSP;
        this.giaSP = giaSP;
        this.motaSP = motaSP;
        this.loaiSP = loaiSP;
        this.hoTenNB = hoTenNB;
        this.sdtNB = sdtNB;
        this.diaGD = diaGD;
    }

    public SanPhamRaoVat() {
    }

    @Override
    public String toString() {
        return "SanPhamRaoVat{" +
                "idNB='" + idNB + '\'' +
                ", tenSP='" + tenSP + '\'' +
                ", giaSP=" + giaSP +
                ", motaSP='" + motaSP + '\'' +
                ", loaiSP='" + loaiSP + '\'' +
                ", hoTenNB='" + hoTenNB + '\'' +
                ", sdtNB='" + sdtNB + '\'' +
                '}';
    }
}
