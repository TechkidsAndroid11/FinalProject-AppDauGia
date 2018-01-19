package com.example.haihm.shelf.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.ProductDetailActivity;
import com.example.haihm.shelf.event.OnClickProductEvent;
import com.example.haihm.shelf.model.SanPhamRaoVat;
import com.example.haihm.shelf.utils.ImageUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Son Hoang on 1/9/2018.
 */

public class ProductTypeAdapter extends RecyclerView.Adapter<ProductTypeAdapter.ItemTypeViewHolder> {
    private static final String TAG = ProductTypeAdapter.class.toString();
    List<SanPhamRaoVat> sanPhamRaoVatList;
    Context context;
    public ProductTypeAdapter(List<SanPhamRaoVat> sanPhamRaoVatList,Context context) {
        this.sanPhamRaoVatList = sanPhamRaoVatList;
        this.context = context;
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
        View iview;

        public ItemTypeViewHolder(View itemView) {
            super(itemView);
            iview =itemView;
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
        }


        public void setData(final SanPhamRaoVat sanPhamRaoVat) {
            Bitmap bitmap = ImageUtils.base64ToImage(sanPhamRaoVat.anhSP.get(0));
            ivProductImage.setImageBitmap(bitmap);
            tvProductPrice.setText(String.valueOf(sanPhamRaoVat.giaSP));
            iview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().postSticky(new OnClickProductEvent(sanPhamRaoVat));
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    context.startActivity(intent);
                    Log.d(TAG, "onClick: ");
                }
            });
            Log.d(TAG, "setData: " + sanPhamRaoVat.giaSP);
        }
    }
}
