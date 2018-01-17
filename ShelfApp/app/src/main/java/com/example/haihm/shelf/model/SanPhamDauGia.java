package com.example.haihm.shelf.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Trần_Tân on 08/01/2018.
 */

public class SanPhamDauGia extends SanPhamRaoVat {
    public double buocGia;
    public double giaCaoNhat;
    public int thoiGian;
    public NguoiMua nguoiMua;

    public SanPhamDauGia() {
    }

    public SanPhamDauGia(String idNB, String tenSP, HashMap<String,String> anhSP, double giaSP, String motaSP, String loaiSP,
                         String hoTenNB, String sdtNB, String diaGD,
                         double buocGia, double giaCaoNhat, int thoiGian, NguoiMua nguoiMua) {
        super(idNB, tenSP, anhSP, giaSP, motaSP, loaiSP, hoTenNB, sdtNB, diaGD);
        this.buocGia = buocGia;
        this.giaCaoNhat = giaCaoNhat;
        this.thoiGian = thoiGian;
        this.nguoiMua = nguoiMua;
    }

    public static class NguoiMua {
        public String id;
        public String ten;
        public String avatar;

        public NguoiMua() {
        }
    }
}
