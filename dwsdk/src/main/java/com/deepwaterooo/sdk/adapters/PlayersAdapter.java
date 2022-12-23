package com.deepwaterooo.sdk.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.appconfig.Numerics;
import com.deepwaterooo.sdk.beans.PlayerDO;

import java.util.ArrayList;

/**
 * Class used to display the login user players in Grid view
 */
public class PlayersAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<PlayerDO> mPlayerDOs;
    LayoutInflater inflater;

    public PlayersAdapter(Context context, ArrayList<PlayerDO> playerDOs) {
        mContext = context;
        mPlayerDOs = playerDOs;
        inflater = (LayoutInflater) mContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mPlayerDOs == null)
            return Numerics.ZERO;

        return mPlayerDOs.size();
    }

    @Override
    public PlayerDO getItem(int position) {
        return mPlayerDOs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.add_first_player, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.tvAddPlayer);
            holder.imageView = (ImageView) row.findViewById(R.id.ivAvatar);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        holder.titleTextView.setTextColor(mContext.getResources().getColor(R.color.white));
        holder.titleTextView.setText(mPlayerDOs.get(position).getFirstName());

        if (!TextUtils.isEmpty(mPlayerDOs.get(position).getProfileURL())) {
// 这里可能是要生成一个圆形的头像,暂时不管
//            Picasso.with(mContext).load(mPlayerDOs.get(position).getProfileURL()).placeholder(R.drawable.default_avatar_circle).
//                transform(new CircleTransform()).into(holder.imageView);

        } else {
//            Picasso.with(mContext).load(R.drawable.default_avatar).
//                transform(new CircleTransform()).into(holder.imageView);
        }

        return row;
    }

    public void refreshData(ArrayList<PlayerDO> playerDOs) {
        mPlayerDOs = playerDOs;
        notifyDataSetChanged();
    }


}
