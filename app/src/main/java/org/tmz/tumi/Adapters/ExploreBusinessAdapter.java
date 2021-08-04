package org.tmz.tumi.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.tmz.tumi.Main.Explore.ExploreActivity;
import org.tmz.tumi.Main.Explore.FragmentViewBusiness;
import org.tmz.tumi.Objects.BusinessObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.UniversalImageLoader;

import java.util.ArrayList;

public class ExploreBusinessAdapter extends RecyclerView.Adapter<ExploreBusinessAdapter.ExploreBusinessViewHolder> {

    private static final String TAG = "ExploreBusinessAdapter";

    public Context mContext;
    public ArrayList<BusinessObject> arrayList;

    public ExploreBusinessAdapter(Context mContext, ArrayList<BusinessObject> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ExploreBusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_explore_business, parent, false);
        return new ExploreBusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreBusinessViewHolder holder, int position) {
        final BusinessObject object = arrayList.get(position);

        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());

        holder.name.setText(object.getName());
        holder.category.setText(object.getCategory());
        UniversalImageLoader.setImage(object.getProfilePicture(), holder.imageView, null, "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedBusiness = object.getKey();
                Log.e(TAG, "onItemClick: Selected a position -- Key: " + object.getKey());

                Fragment fragment = new FragmentViewBusiness();
                Bundle bundle = new Bundle();
                bundle.putString(mContext.getString(R.string.selectedBusiness), selectedBusiness);
                bundle.putString(mContext.getString(R.string.fromWhere), mContext.getString(R.string.exploreActivity));
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = ((ExploreActivity) v.getContext()).getSupportFragmentManager();

                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out  // popExit
                        )
                        .replace(R.id.frameLayoutExplore, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void filterList(ArrayList<BusinessObject> searchedList) {
        arrayList = searchedList;
        notifyDataSetChanged();
    }

    public class ExploreBusinessViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView name, category;

        public ExploreBusinessViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.viewHolderBusinessPicture);
            name = itemView.findViewById(R.id.viewHolderBusinessNameTextView);
            category = itemView.findViewById(R.id.viewHolderBusinessCategoryTextView);
        }
    }
}
