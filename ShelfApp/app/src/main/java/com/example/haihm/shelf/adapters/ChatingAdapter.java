package com.example.haihm.shelf.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by Trần_Tân on 24/01/2018.
 */

public class ChatingAdapter extends RecyclerView.Adapter<ChatingAdapter.ChatingViewHolder> {
    ArrayList<SanPhamDauGia.Chat> listChat;
    View view;
    Context context;
    public ChatingAdapter(ArrayList<SanPhamDauGia.Chat> listChat,Context context) {
        this.listChat = listChat;
        this.context = context;
    }


    @Override
    public ChatingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.item_recyclerview_chat, parent, false);
        return new ChatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatingViewHolder holder, int position) {
        holder.setData(listChat.get(position+1));
    }

    @Override
    public int getItemCount() {
        return listChat.size()-1;
    }

    public class ChatingViewHolder extends RecyclerView.ViewHolder{
        TextView tvChat;
        ImageView imageView;
        public ChatingViewHolder(View itemView) {
            super(itemView);
            tvChat = itemView.findViewById(R.id.tv_chat);
            imageView = itemView.findViewById(R.id.iv_avatar);
        }
        public void setData(SanPhamDauGia.Chat chat) {
            tvChat.setText(chat.contentMess);
            if(chat.avatarMess.contains("http")){
                Picasso.with(context).load(chat.avatarMess).transform(new CropCircleTransformation()).into(imageView);
            }else {
                try{
                    Bitmap bitmap = ImageUtils.base64ToImage(chat.avatarMess);
                    File f = File.createTempFile("tmp", "png", context.getCacheDir());
                    FileOutputStream fos = new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    Picasso.with(context).load(f).transform(new CropCircleTransformation()).into(imageView);
                }
                catch (Exception e){}
            }
        }
    }
}
