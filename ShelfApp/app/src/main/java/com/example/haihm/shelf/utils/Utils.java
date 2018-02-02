package com.example.haihm.shelf.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.text.NumberFormat;
import java.util.Locale;

;

/**
 * Created by ThangPham on 11/22/2017.
 */

public class Utils {
    public static void openFragment(FragmentManager fragmentManagert, int layoutId, Fragment fragment) {
        // quản lý việc thêm, sửa, xóa or thay thế của Fragment
        FragmentTransaction fragmentTransaction = fragmentManagert.beginTransaction();
        fragmentTransaction.add(layoutId, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public static void openFragmentNotBack(FragmentManager fragmentManagert, int layoutId, Fragment fragment) {
        // quản lý việc thêm, sửa, xóa or thay thế của Fragment
        FragmentTransaction fragmentTransaction = fragmentManagert.beginTransaction();
        fragmentTransaction.add(layoutId, fragment);
        fragmentTransaction.commit();
    }

    public static String formatPrice(double price){
        return NumberFormat.getNumberInstance(Locale.US).format(price) + "đ";
    }

}
