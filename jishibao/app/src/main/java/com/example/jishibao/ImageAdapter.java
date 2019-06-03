package com.example.jishibao;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class ImageAdapter extends  RecyclerView.Adapter<ImageAdapter.ViewHolder>{
    private List<Image> imageList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        public ViewHolder(View v)
        {
            super(v);
            image=(ImageView) v.findViewById(R.id.image_ID);


        }


    }
    public ImageAdapter(List<Image> list)
    {
        imageList=list;
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Image image=imageList.get(position);
        holder.image.setImageBitmap(image.getBitmap());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }
}
