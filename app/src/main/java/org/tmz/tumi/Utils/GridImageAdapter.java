package org.tmz.tumi.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.tmz.tumi.R;

import java.util.ArrayList;

public class GridImageAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final LayoutInflater inflater;
    private final int layoutResource;
    private final String append;
    private final ArrayList<String> imgURLS;
    ImageLoader imageLoader;

    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLS) {
        super(context, layoutResource, imgURLS);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.layoutResource = layoutResource;
        this.append = append;
        this.imgURLS = imgURLS;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        /*ViewHolder build pattern -  THis is similar to a RecyclerView*/
        if (convertView == null) {
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.exploreGridImageView);
            holder.progressBar = convertView.findViewById(R.id.exploreProgressBar);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String imageURL = getItem(position);

        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imageURL, holder.image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (holder.progressBar != null) {
                    holder.progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (holder.progressBar != null) {
                    holder.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (holder.progressBar != null) {
                    holder.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (holder.progressBar != null) {
                    holder.progressBar.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        SquareImageView image;
        ProgressBar progressBar;
    }
}
