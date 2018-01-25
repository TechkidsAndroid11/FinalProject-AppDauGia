package com.example.haihm.shelf.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.AuctionDetailsActivity;
import com.example.haihm.shelf.activity.ProductDetailActivity;
import com.example.haihm.shelf.event.OnClickAuctionEvent;
import com.example.haihm.shelf.event.OnClickProductEvent;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.utils.ImageUtils;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Son Hoang on 1/24/2018.
 */

public class AuctionProductAdapter extends RecyclerView.Adapter<AuctionProductAdapter.AuctionProductViewHolder> {
    private static final String TAG = AuctionProductAdapter.class.toString();
    List<SanPhamDauGia> sanPhamDauGiaList;
    View view;
    Context context;
    public AuctionProductAdapter(List<SanPhamDauGia> sanPhamDauGiaList,Context context) {
        this.sanPhamDauGiaList = sanPhamDauGiaList;
        this.context = context;
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
        View iview;
        public AuctionProductViewHolder(View itemView) {
            super(itemView);
            iview = itemView;
            ivAuctionImage = itemView.findViewById(R.id.iv_auction_image);
            tvAuctionPrice = itemView.findViewById(R.id.tv_product_price);
        }

        public void setData(final SanPhamDauGia sanPhamDauGia) {
            Bitmap bitmap = ImageUtils.base64ToImage(sanPhamDauGia.anhSP.get(0));
            ivAuctionImage.setImageBitmap(bitmap);
//            tvAuctionPrice.setText(String.valueOf(sanPhamDauGia.giaSP));
            iview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().postSticky(new OnClickAuctionEvent(sanPhamDauGia));
                    Intent intent = new Intent(context, AuctionDetailsActivity.class);
                    context.startActivity(intent);
                    Log.d(TAG, "onClick: ");
                }
            });
            Log.d(TAG, "setData: " + sanPhamDauGia.giaSP);
        }
    }
}
