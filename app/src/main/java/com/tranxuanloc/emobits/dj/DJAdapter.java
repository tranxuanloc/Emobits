package com.tranxuanloc.emobits.dj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tranxuanloc.emobits.R;

import java.text.NumberFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tranxuanloc on 4/6/2016.
 */
public class DJAdapter extends ArrayAdapter<DJInfo.ListDJInfo> {
    private LayoutInflater layoutInflater;

    public DJAdapter(Context context, ArrayList<DJInfo.ListDJInfo> objects) {
        super(context, 0, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DJHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_dj, parent, false);
            holder = new DJHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (DJHolder) convertView.getTag();
        DJInfo.ListDJInfo info = getItem(position);
        holder.tvName.setText(info.getName());
        holder.tvScore.setText(NumberFormat.getInstance().format(info.getTotal()));

        return convertView;
    }

    static class DJHolder {
        @Bind(R.id.item_dj_tv_name)
        TextView tvName;
        @Bind(R.id.item_dj_tv_score)
        TextView tvScore;

        public DJHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}
