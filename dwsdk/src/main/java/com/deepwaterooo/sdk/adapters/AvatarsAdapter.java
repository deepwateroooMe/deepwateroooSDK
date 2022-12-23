package com.deepwaterooo.sdk.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.Numerics;
import com.deepwaterooo.sdk.beans.AvatarsDO;
import com.deepwaterooo.sdk.utils.RoundedCornersTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Class used to display the avatars in rounded shape
 */
public class AvatarsAdapter extends BaseAdapter {

    private List<AvatarsDO> mAvatars;
    private Context mContext;
    private Transformation transformation = new RoundedCornersTransformation(Numerics.TEN, Numerics.TEN);

    public AvatarsAdapter(Context context, List<AvatarsDO> avatars) {
        mAvatars = avatars;
        mContext = context;
    }

    @Override
    public int getCount() {

        if (mAvatars == null)
            return 0;

        return mAvatars.size();
    }

    @Override
    public Object getItem(int position) {
        return mAvatars.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ImageView ivAvatar;

        if (convertView == null) {
            ivAvatar = new ImageView(mContext);

            ivAvatar.post(new Runnable() {
                    @Override
                    public void run() {
                        GridView.LayoutParams params = (GridView.LayoutParams) ivAvatar.getLayoutParams();
                        params.width = (int) (Constants.getDeviceWidth() * 0.06);
                        params.height = (int) (Constants.getDeviceWidth() * 0.06);
                        ivAvatar.setLayoutParams(params);
                    }
                });

        } else {
            ivAvatar = (ImageView) convertView;
        }

        ivAvatar.setTag(mAvatars.get(position));

        if (!TextUtils.isEmpty(mAvatars.get(position).getAssetURL())) {
            Picasso.with(mContext).load(mAvatars.get(position).getAssetURL()).transform(transformation).into(ivAvatar);
        } else {
            Picasso.with(mContext).load(R.drawable.add_player).transform(transformation).into(ivAvatar);
        }
        return ivAvatar;
    }

    public void refreshData(List<AvatarsDO> avatars) {
        mAvatars = avatars;
        notifyDataSetChanged();
    }
}