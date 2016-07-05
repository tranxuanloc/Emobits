package com.tranxuanloc.emobits.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tranxuanloc.emobits.R;

import java.util.ArrayList;

/**
 * Created by tranxuanloc on 4/8/2016.
 */
public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SearchViewHolder> {
    private final float v;
    private ArrayList<SessionInfo.ListSessionInfo> arrayList;
    private Context context;

    public SessionAdapter(Context context, ArrayList<SessionInfo.ListSessionInfo> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        v = context.getResources().getDisplayMetrics().density * 120;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        SessionInfo.ListSessionInfo info = arrayList.get(position);
        holder.tvName.setText(info.getSongName());
        holder.tvEmotion.setText(info.getDJStyle());
        Picasso.with(context).load(info.getThumbnailURL()).resize((int) v, (int) v).into(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvEmotion;
        private ImageView ivThumbnail;

        public SearchViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.item_tv_session_name);
            tvEmotion = (TextView) itemView.findViewById(R.id.item_tv_session_emotion);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.item_iv_session_thumbnail);
        }
    }
}
