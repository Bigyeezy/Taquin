package org.esgi.taquin;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

class ImageAdapter extends BaseAdapter {
    private final Context mContext;

    private final Integer[] mThumbIds = {

            R.drawable.pic0, R.drawable.pic1,
            R.drawable.pic2, R.drawable.pic3,
            R.drawable.pic4, R.drawable.pic5,
            R.drawable.pic6, R.drawable.pic7,
            R.drawable.pic8, R.drawable.pic9
    };

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return mThumbIds[position];
    }

    public long getItemId(int position) {
        return mThumbIds[position];
    }

    // crée une nouvelle imagViem pour chaque élément référencé par l'adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }
}