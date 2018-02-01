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
import com.example.haihm.shelf.activity.ProductDetailActivity;
import com.example.haihm.shelf.event.OnClickProductEvent;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.model.SanPhamRaoVat;
import com.example.haihm.shelf.utils.ImageUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Trần_Tân on 01/02/2018.
 */

public class HistoryProductAdapter extends RecyclerView.Adapter<HistoryProductAdapter.HistoryProductViewHolder> {
    ArrayList<SanPhamRaoVat> lsanPhamRaoVat;
    private View view;
    Context context;
    public HistoryProductAdapter(ArrayList<SanPhamRaoVat> lsanPhamRaoVat,Context context) {
        this.lsanPhamRaoVat = lsanPhamRaoVat;
        this.context = context;
    }

    @Override
    public HistoryProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.item_history_product, parent, false);
        return new HistoryProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryProductViewHolder holder, int position) {
        holder.setData(lsanPhamRaoVat.get(position));
    }

    @Override
    public int getItemCount() {
        return lsanPhamRaoVat.size();
    }


    public class HistoryProductViewHolder extends RecyclerView.ViewHolder{
        TextView tvName,tvCost;
        ImageView ivImage;
        public HistoryProductViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvCost = itemView.findViewById(R.id.tv_product_price);
            ivImage = itemView.findViewById(R.id.iv_product_image);
        }
        public void setData(final SanPhamRaoVat data){
            tvName.setText(data.tenSP);
            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            decimalFormat.applyPattern("#,###,###");

            tvCost.setText(decimalFormat.format(data.giaSP)+"đ");

            ivImage.setImageBitmap(ImageUtils.base64ToImage(data.anhSP.get(0)));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().postSticky(new OnClickProductEvent(data));
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }
}
