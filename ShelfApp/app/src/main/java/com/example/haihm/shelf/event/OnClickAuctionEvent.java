package com.example.haihm.shelf.event;

import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.model.UserModel;

/**
 * Created by Trần_Tân on 18/01/2018.
 */

public class OnClickAuctionEvent {
    public SanPhamDauGia sanPhamDauGia;
    public UserModel userModel;

    public OnClickAuctionEvent(SanPhamDauGia sanPhamDauGia, UserModel userModel) {
        this.sanPhamDauGia = sanPhamDauGia;
        this.userModel = userModel;
    }
}
