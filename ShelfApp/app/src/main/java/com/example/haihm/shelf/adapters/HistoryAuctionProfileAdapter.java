package com.example.haihm.shelf.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.AuctionDetailsActivity;
import com.example.haihm.shelf.event.OnClickAuctionEvent;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.model.SanPhamRaoVat;
import com.example.haihm.shelf.utils.ImageUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by ThangPham on 2/7/2018.
 */

public class HistoryAuctionProfileAdapter extends RecyclerView.Adapter<HistoryAuctionProfileAdapter.HistoryAuctionViewHolder> {
    ArrayList<SanPhamDauGia> lsanPhamDauGia;
    private View view;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public HistoryAuctionProfileAdapter(ArrayList<SanPhamDauGia> lsanPhamDauGia,Context context) {
        this.lsanPhamDauGia = lsanPhamDauGia;
        this.context = context;
    }

    @Override
    public HistoryAuctionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.item_history_auction, parent, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        return new HistoryAuctionProfileAdapter.HistoryAuctionViewHolder(view);
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
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    checkRemoveProduct(data);

                    return true;
                }
            });
        }
    }
    private void checkRemoveProduct(final SanPhamDauGia data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Bạn có muốn xóa phiên đấu giá này không?");
        builder.setNeutralButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child("Auction").child(data.loaiSP).child(data.idSP).removeValue();
                Toast.makeText(context, "Xóa phiên đấu giá thành công", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }
}
