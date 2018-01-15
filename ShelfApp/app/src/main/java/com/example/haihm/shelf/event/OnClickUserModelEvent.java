package com.example.haihm.shelf.event;

import com.example.haihm.shelf.model.UserModel;

/**
 * Created by ThangPham on 1/14/2018.
 */

public class OnClickUserModelEvent {
    public UserModel userModel;
    public OnClickUserModelEvent(UserModel userModel)
    {
        this.userModel=userModel;
    }
}
