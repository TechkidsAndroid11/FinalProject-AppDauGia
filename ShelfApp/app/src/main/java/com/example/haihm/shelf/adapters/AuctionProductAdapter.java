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
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.utils.ImageUtils;


import java.util.List;

/**
 * Created by Son Hoang on 1/24/2018.
 */

public class AuctionProductAdapter extends RecyclerView.Adapter<AuctionProductAdapter.AuctionProductViewHolder> {
    private static final String TAG = AuctionProductAdapter.class.toString();
    List<SanPhamDauGia> sanPhamDauGiaList;
    View view;

    public AuctionProductAdapter(List<SanPhamDauGia> sanPhamDauGiaList) {
        this.sanPhamDauGiaList = sanPhamDauGiaList;
    }

    @Override
    public AuctionProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.list_auction_product, parent, false);

        return new AuctionProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AuctionProductViewHolder holder, int position) {
        holder.setData(sanPhamDauGiaList.get(position));
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + sanPhamDauGiaList.size());
        return sanPhamDauGiaList.size();
    }

    public class AuctionProductViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivAuctionImage;
        private TextView tvAuctionPrice;

        public AuctionProductViewHolder(View itemView) {
            super(itemView);
            ivAuctionImage = itemView.findViewById(R.id.iv_auction_image);
            tvAuctionPrice = itemView.findViewById(R.id.tv_product_price);
        }

        public void setData(SanPhamDauGia sanPhamDauGia) {

                Bitmap bitmap = ImageUtils.base64ToImage(sanPhamDauGia.anhSP.get(0));
            ivAuctionImage.setImageBitmap(bitmap);
//            tvAuctionPrice.setText(String.valueOf(sanPhamDauGia.giaSP));
            Log.d(TAG, "setData: " + sanPhamDauGia.giaSP);
        }
    }
}
