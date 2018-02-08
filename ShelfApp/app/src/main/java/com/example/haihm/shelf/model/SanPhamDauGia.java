package com.example.haihm.shelf.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Trần_Tân on 08/01/2018.
 */

public class SanPhamDauGia extends SanPhamRaoVat {

    public double buocGia;
    public double giaCaoNhat;
    public Date tgianKthuc;
    public String nguoiMua;
    public ArrayList<Chat> lchat;

    public SanPhamDauGia() {
    }

    public SanPhamDauGia(String tenSP, ArrayList<String> anhSP, double giaSP,
                         String motaSP, String loaiSP, String diaGD, String nguoiB,
                         double buocGia, double giaCaoNhat, Date tgianKthuc, String nguoiMua, ArrayList<Chat> lchat) {
        super(tenSP, anhSP, giaSP, motaSP, loaiSP, diaGD, nguoiB);
        this.buocGia = buocGia;
        this.giaCaoNhat = giaCaoNhat;
        this.tgianKthuc = tgianKthuc;
        this.nguoiMua = nguoiMua;
        this.lchat = lchat;
    }

    public SanPhamDauGia(double buocGia, double giaCaoNhat, Date tgianKthuc, String nguoiMua, ArrayList<Chat> lchat) {
        this.buocGia = buocGia;
        this.giaCaoNhat = giaCaoNhat;
        this.tgianKthuc = tgianKthuc;
        this.nguoiMua = nguoiMua;
        this.lchat = lchat;
    }

    public static class Chat{
        public String nameMess;
        public String avatarMess;
        public String contentMess;

        public Chat() {
        }
    }
}
