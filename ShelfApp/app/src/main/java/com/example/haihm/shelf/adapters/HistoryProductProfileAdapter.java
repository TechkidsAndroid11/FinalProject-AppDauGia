package com.example.haihm.shelf.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.ProductDetailActivity;
import com.example.haihm.shelf.event.OnClickProductEvent;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.SanPhamRaoVat;
import com.example.haihm.shelf.model.UserModel;
import com.example.haihm.shelf.utils.ImageUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by ThangPham on 2/7/2018.
 */

public class HistoryProductProfileAdapter extends RecyclerView.Adapter<HistoryProductProfileAdapter.HistoryProductViewHolder> {
    private static final String TAG = "HistoryProductAdapter";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<SanPhamRaoVat> lsanPhamRaoVat;
    UserModel userModel;
    private View view;
    Context context;

    public HistoryProductProfileAdapter(ArrayList<SanPhamRaoVat> lsanPhamRaoVat, Context context,UserModel userModel) {

        this.lsanPhamRaoVat = lsanPhamRaoVat;
        this.context = context;
    }

    @Override
    public HistoryProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.item_history_product, parent, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        return new HistoryProductProfileAdapter.HistoryProductViewHolder(view);
    }


    @Override
    public void onBindViewHolder(HistoryProductViewHolder holder, int position) {
        holder.setData(lsanPhamRaoVat.get(position));
        for (int i = 0; i < lsanPhamRaoVat.size(); i++) {
            Log.d(TAG, "onBindViewHolder: " + lsanPhamRaoVat.get(i).tenSP + " - " + lsanPhamRaoVat.get(i).loaiSP + " - " + lsanPhamRaoVat.get(i).nguoiB);
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + lsanPhamRaoVat.size());
        return lsanPhamRaoVat.size();
    }


    public class HistoryProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCost;
        ImageView ivImage;

        public HistoryProductViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_product_name);
            tvCost = itemView.findViewById(R.id.tv_product_price);
            ivImage = itemView.findViewById(R.id.iv_product_image);

        }

        public void setData(final SanPhamRaoVat data) {
            tvName.setText(data.tenSP);
            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            decimalFormat.applyPattern("#,###,###");

            tvCost.setText(decimalFormat.format(data.giaSP) + "đ");

            ivImage.setImageBitmap(ImageUtils.base64ToImage(data.anhSP.get(0)));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().postSticky(new OnClickProductEvent(data));
                    Intent intent = new Intent(context, ProductDetailActivity.class);
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

    private void checkRemoveProduct(final SanPhamRaoVat data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Bạn có muốn xóa sản phẩm này không?");
        builder.setNeutralButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child("Product").child(data.loaiSP).child(data.idSP).removeValue();
                Toast.makeText(context, "Xóa sản phảm thành công", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

}
