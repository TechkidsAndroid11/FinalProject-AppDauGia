package com.example.haihm.shelf.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Trần_Tân on 08/01/2018.
 */

public class SanPhamDauGia extends SanPhamRaoVat {
    public String idSP;
    public double buocGia;
    public double giaCaoNhat;
    public Date tgianKthuc;
    public String nguoiMua;
    public ArrayList<Chat> lchat;

    public SanPhamDauGia() {
    }

    public SanPhamDauGia(String nguoiB, String tenSP, ArrayList<String> anhSP, double giaSP,
                         String motaSP, String loaiSP, String diaGD, double buocGia, double giaCaoNhat,
                         Date date, String nguoiMua, ArrayList<Chat> lchat) {
        super(nguoiB, tenSP, anhSP, giaSP, motaSP, loaiSP, diaGD);
        this.buocGia = buocGia;
        this.giaCaoNhat = giaCaoNhat;
        this.tgianKthuc = date;
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
