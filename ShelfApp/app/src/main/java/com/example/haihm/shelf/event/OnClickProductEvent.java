package com.example.haihm.shelf.event;

import com.example.haihm.shelf.model.SanPhamRaoVat;

/**
 * Created by Trần_Tân on 17/01/2018.
 */

public class OnClickProductEvent {
    public SanPhamRaoVat sanPhamRaoVat;

    public OnClickProductEvent(SanPhamRaoVat sanPhamRaoVat) {
        this.sanPhamRaoVat = sanPhamRaoVat;
    }
}
