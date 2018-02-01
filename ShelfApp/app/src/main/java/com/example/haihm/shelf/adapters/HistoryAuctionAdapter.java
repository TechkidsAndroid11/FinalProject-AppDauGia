package com.example.haihm.shelf.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.AuctionDetailsActivity;
import com.example.haihm.shelf.event.OnClickAuctionEvent;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.utils.ImageUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Trần_Tân on 01/02/2018.
 */

public class HistoryAuctionAdapter extends RecyclerView.Adapter<HistoryAuctionAdapter.HistoryAuctionViewHolder> {
    ArrayList<SanPhamDauGia> lsanPhamDauGia;
    private View view;
    Context context;
    public HistoryAuctionAdapter(ArrayList<SanPhamDauGia> lsanPhamDauGia,Context context) {
        this.lsanPhamDauGia = lsanPhamDauGia;
        this.context = context;
    }

    @Override
    public HistoryAuctionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.item_history_auction, parent, false);
        return new HistoryAuctionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryAuctionViewHolder holder, int position) {
        holder.setData(lsanPhamDauGia.get(position));
    }

    @Override
    public int getItemCount() {
        return lsanPhamDauGia.size();
    }


    public class HistoryAuctionViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStartCost, tvHighestCost;
        ImageView ivImage;

        public HistoryAuctionViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvStartCost = itemView.findViewById(R.id.tv_start_cost);
            tvHighestCost = itemView.findViewById(R.id.tv_highest_cost);
            ivImage = itemView.findViewById(R.id.iv_image);
        }

        public void setData(final SanPhamDauGia data) {
            tvName.setText(data.tenSP);
            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            decimalFormat.applyPattern("#,###,###");

            tvStartCost.setText(decimalFormat.format(data.giaSP) + "đ");
            tvHighestCost.setText(decimalFormat.format(data.giaCaoNhat) + "đ");

            ivImage.setImageBitmap(ImageUtils.base64ToImage(data.anhSP.get(0)));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().postSticky(new OnClickAuctionEvent(data));
                    Intent intent = new Intent(context, AuctionDetailsActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }
}