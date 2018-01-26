package com.app.aqoong.smsreport.data;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.aqoong.smsreport.R;
import com.app.aqoong.smsreport.ui.FindDetailActivity;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by aqoong on 2017. 10. 11..
 */

public class FindListAdapter extends RecyclerView.Adapter<FindListAdapter.ViewHolder> {
    private Context mContext = null;

    private ArrayList<ProductData> mDatas = null;
    private ArrayList<ProductData> list = null;


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextName;          //product name
        public TextView mTextGetPlace;      //saved place
        public TextView mTextGetDate;       //get date

        public ViewHolder(View itemView) {
            super(itemView);

            mTextName = (TextView)itemView.findViewById(R.id.item_name);
            mTextGetPlace = (TextView)itemView.findViewById(R.id.item_place);
            mTextGetDate = (TextView)itemView.findViewById(R.id.item_date);
        }
    }

    public FindListAdapter(Context _context,ArrayList<ProductData> datas){

        mDatas = datas;
        list = new ArrayList<ProductData>();
        list.addAll(mDatas);
        mContext = _context;
    }

    @Override
    public FindListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        //set the view's size, margins....

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int p) {
        final int position = p;
        holder.mTextName.setText(mDatas.get(p).name);
        holder.mTextGetPlace.setText(mDatas.get(p).takePlace);
        holder.mTextGetDate.setText(mDatas.get(p).date);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FindDetailActivity.class);
                intent.putExtra("DATA", mDatas.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setItemList(ArrayList<ProductData> _list){
        while(list.size() > 0){
            list.remove(list.size()-1);
        }
        list.addAll(_list);
    }

    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());

        mDatas.clear();
        if (charText.length() == 0) {
            mDatas.addAll(list);
        } else {

            for(int i = 0 ; i < list.size() ; i++){
                ProductData tempData = list.get(i);

                String name = tempData.name;
                String category = tempData.category;
                String takePlace = tempData.takePlace;
                String getPlace = tempData.place;
                String getPosition = tempData.position;

                if (name.toLowerCase().contains(charText) ||
                        category.toLowerCase().contains(charText) ||
                        takePlace.toLowerCase().contains(charText) ||
                        getPlace.toLowerCase().contains(charText) ||
                        getPosition.toLowerCase().contains(charText)) {
                    mDatas.add(tempData);
                }
            }

        }
        notifyDataSetChanged();

    }

}
