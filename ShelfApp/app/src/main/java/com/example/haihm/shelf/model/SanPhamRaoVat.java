package com.example.haihm.shelf.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Trần_Tân on 08/01/2018.
 */

public class SanPhamRaoVat {
    public String tenSP;
    public ArrayList<String> anhSP ;
    public double giaSP;
    public String motaSP;
    public String loaiSP;
    public String diaGD;
    public UserModel nguoiB;
    public SanPhamRaoVat(UserModel nguoiB, String tenSP, ArrayList<String> anhSP, double giaSP,
                         String motaSP, String loaiSP, String diaGD) {
        this.nguoiB = nguoiB;
        this.tenSP = tenSP;
        this.anhSP = anhSP;
        this.giaSP = giaSP;
        this.motaSP = motaSP;
        this.loaiSP = loaiSP;
        this.diaGD = diaGD;
    }

    public SanPhamRaoVat() {

    }

    @Override
    public String toString() {
        return "SanPhamRaoVat{" +
                ", tenSP='" + tenSP + '\'' +
                ", giaSP=" + giaSP +
                ", motaSP='" + motaSP + '\'' +
                ", loaiSP='" + loaiSP + '\'' +
                '}';
    }
}
