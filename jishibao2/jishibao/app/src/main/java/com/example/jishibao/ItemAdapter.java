package com.example.jishibao;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> mItemList;

    public interface OnremoveListnner{
        void  ondelect(int i);
    }

    private OnremoveListnner onremoveListnner;

    public void setOnremoveListnner(OnremoveListnner onremoveListnner) {
        this.onremoveListnner = onremoveListnner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_item,viewGroup,false);

      final  ViewHolder holder=new ViewHolder(view);
      holder.itemview.setOnClickListener(new View.OnClickListener(){
          public void onClick(View v)
          {
              int position=holder.getAdapterPosition();
              Item it=mItemList.get(position);
              Toast.makeText(v.getContext(),"you clicked "+it.getTitle(),Toast.LENGTH_SHORT).show();
          }
      });
        return  holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Item item=mItemList.get(i);
        viewHolder.title.setText(item.getTitle());
        viewHolder.body.setText(item.getBody());
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (onremoveListnner!=null){
                    onremoveListnner.ondelect(i);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View itemview;
        TextView title;
        TextView body;
        public ViewHolder (View view)
        {
            super(view);
            itemview=view;
            title=(TextView)view.findViewById(R.id.title);
            body=(TextView)view.findViewById(R.id.body);

        }
    }
public ItemAdapter(List<Item> itemList){
        mItemList=itemList;
}



}
