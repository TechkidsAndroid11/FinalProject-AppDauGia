package com.example.haihm.shelf.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.event.OnClickAddPhotoEvent;
import com.example.haihm.shelf.utils.ImageUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Trần_Tân on 09/01/2018.
 */

public class ThemAnhSPAdapter  extends RecyclerView.Adapter<ThemAnhSPAdapter.AnhSPViewHolder> {
    private static final String TAG = "ThemAnhSPAdapter";
    List<String> lanhSP;
    Context context;
    public ThemAnhSPAdapter(List<String> anhSP,Context context) {
        this.lanhSP = anhSP;
    }

    @Override
    public AnhSPViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_recyclerview_add_photo,parent,false);

        return new AnhSPViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AnhSPViewHolder holder, int position) {
        boolean lastItem = (position+1 ==lanhSP.size());
        holder.setData(lanhSP.get(position),lastItem);

    }

    @Override
    public int getItemCount() {
        return lanhSP.size();
    }

    public class AnhSPViewHolder extends RecyclerView.ViewHolder {
        ImageView imSP;
        View view;

        public AnhSPViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imSP = itemView.findViewById(R.id.iv_anh_sp);
        }

        public void setData(String base64Image, boolean lastItem) {
            imSP.setImageBitmap(ImageUtils.base64ToImage(base64Image));
            if (lastItem) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: ");
                        EventBus.getDefault().postSticky(new OnClickAddPhotoEvent());
                    }
                });
            }
        }
    }
}
