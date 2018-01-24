package com.example.haihm.shelf.event;

import com.example.haihm.shelf.model.UserModel;

/**
 * Created by Trần_Tân on 10/01/2018.
 */

public class OnClickAddSanPhamEvent {
    public UserModel userModel;

    public OnClickAddSanPhamEvent(UserModel userModel) {
        this.userModel = userModel;
    }
}
