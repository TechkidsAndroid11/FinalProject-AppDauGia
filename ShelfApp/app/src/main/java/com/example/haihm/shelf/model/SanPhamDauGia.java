package com.example.haihm.shelf.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Trần_Tân on 08/01/2018.
 */

public class SanPhamDauGia extends SanPhamRaoVat {
    public String idSP;
    public double buocGia;
    public double giaCaoNhat;
    public Date tgianKthuc;
    public UserModel nguoiMua;
    public Chat chat;

    public SanPhamDauGia() {
    }

    public SanPhamDauGia(UserModel nguoiB, String tenSP, ArrayList<String> anhSP, double giaSP,
                         String motaSP, String loaiSP, String diaGD, double buocGia, double giaCaoNhat,
                         Date date, UserModel nguoiMua, Chat chat) {
        super(nguoiB, tenSP, anhSP, giaSP, motaSP, loaiSP, diaGD);
        this.buocGia = buocGia;
        this.giaCaoNhat = giaCaoNhat;
        this.tgianKthuc = date;
        this.nguoiMua = nguoiMua;
        this.chat = chat;
    }
    public static class Chat{
        public String nameMess;
        public String avatarMess;
        public String contentMess;

        public Chat() {
        }
    }
}
