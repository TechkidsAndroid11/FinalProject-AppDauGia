package com.example.haihm.shelf.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.model.SanPhamRaoVat;
import com.example.haihm.shelf.utils.ImageUtils;

import java.util.List;

/**
 * Created by Son Hoang on 1/9/2018.
 */

public class ShoppingProductAdapter extends RecyclerView.Adapter<ShoppingProductAdapter.ItemTypeViewHolder> {
    private static final String TAG = ShoppingProductAdapter.class.toString();
    List<SanPhamRaoVat> sanPhamRaoVatList;
    View view;

    public ShoppingProductAdapter(List<SanPhamRaoVat> sanPhamRaoVatList) {
        this.sanPhamRaoVatList = sanPhamRaoVatList;
    }

    @Override
    public ItemTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.list_shopping_product, parent, false);

        return new ItemTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemTypeViewHolder holder, int position) {
        holder.setData(sanPhamRaoVatList.get(position));
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + sanPhamRaoVatList.size());
        return sanPhamRaoVatList.size();
    }

    public class ItemTypeViewHolder extends RecyclerView.ViewHolder{
        ImageView ivProductImage;
        TextView tvProductPrice;


        public ItemTypeViewHolder(View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
        }


        public void setData(SanPhamRaoVat sanPhamRaoVat) {
            Bitmap bitmap = ImageUtils.base64ToImage(sanPhamRaoVat.anhSP.get(0));
            ivProductImage.setImageBitmap(bitmap);
            tvProductPrice.setText(String.valueOf(sanPhamRaoVat.giaSP));
            Log.d(TAG, "setData: " + sanPhamRaoVat.giaSP);
        }
    }
}
