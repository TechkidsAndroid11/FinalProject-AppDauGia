package com.example.haihm.shelf.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.model.SanPhamRaoVat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Son Hoang on 1/9/2018.
 */

public class ProductTypeAdapter extends RecyclerView.Adapter<ProductTypeAdapter.ItemTypeViewHolder> {
    private static final String TAG = ProductTypeAdapter.class.toString();
    List<SanPhamRaoVat> sanPhamRaoVatList;

    public ProductTypeAdapter(List<SanPhamRaoVat> sanPhamRaoVatList) {
        this.sanPhamRaoVatList = sanPhamRaoVatList;
    }

    @Override
    public ItemTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_type, parent, false);

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
            tvProductPrice.setText(String.valueOf(sanPhamRaoVat.giaSP));
            Log.d(TAG, "setData: " + sanPhamRaoVat.giaSP);
        }
    }
}
