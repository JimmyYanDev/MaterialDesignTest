package com.micheal_yan.materialdesigntest.UI;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.micheal_yan.materialdesigntest.R;
import com.micheal_yan.materialdesigntest.model.ZhihuModel;

import java.util.List;

/**
 * Created by micheal-yan on 2017/4/3.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "MyRecyclerViewAdapter";
    private Context mContext;
    private List<ZhihuModel.StoriesBean> data;

    public MyRecyclerViewAdapter(List<ZhihuModel.StoriesBean> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.story_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                ZhihuModel.StoriesBean item = data.get(pos);
                Log.e(TAG, "onClick: " + item.getTitle());
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(DetailActivity.STORIE_TITLE, item.getTitle());
                intent.putExtra(DetailActivity.STORIE_ID, item.getId());
                intent.putExtra(DetailActivity.STORIE_IMAGE, item.getImages().get(0));
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ZhihuModel.StoriesBean resultsBean = data.get(position);
        Log.e(TAG, resultsBean.getTitle());
        Glide.with(mContext).load(resultsBean.getImages().get(0)).into(holder.mImageView);
        holder.mTextView.setText(resultsBean.getTitle());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        ImageView mImageView;
        TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView;
            mImageView = (ImageView) itemView.findViewById(R.id.image_icon);
            mTextView = (TextView) itemView.findViewById(R.id.image_desc);
        }
    }
}
