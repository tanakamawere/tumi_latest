package org.tmz.tumi.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.tmz.tumi.Main.Explore.ExploreActivity;
import org.tmz.tumi.Main.Explore.FragmentViewAd;
import org.tmz.tumi.Objects.AdvertisementsObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.SquareImageView;
import org.tmz.tumi.Utils.UniversalImageLoader;

import java.util.ArrayList;

public class ExploreProductsAdapter extends RecyclerView.Adapter<ExploreProductsAdapter.ExploreProductsViewHolder> {

    private static final String TAG = "ExploreProductsAdapter";
    private final Context context;
    private ArrayList<AdvertisementsObject> arrayList;
    private ArrayList<AdvertisementsObject> arrayListSearch;

    public ExploreProductsAdapter(final ArrayList<AdvertisementsObject> arrayList, Context context) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ExploreProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_explore_grid, parent, false);

        return new ExploreProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreProductsViewHolder holder, final int position) {
        final AdvertisementsObject object = arrayList.get(position);

        UniversalImageLoader universalImageLoader = new UniversalImageLoader(context);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());

        UniversalImageLoader.setImage(object.getImageURL(), holder.imageView,
                null, "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedProduct = object.getAdKey();
                Log.e(TAG, "onItemClick: Selected a position -- Key: " + object.getAdKey());

                Fragment fragment = new FragmentViewAd();
                Bundle bundle = new Bundle();
                bundle.putString(context.getString(R.string.selectedProductGridView), selectedProduct);
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

    public void filterList(ArrayList<AdvertisementsObject> arraySearch) {
        arrayList = arraySearch;
        notifyDataSetChanged();
    }

    public class ExploreProductsViewHolder extends RecyclerView.ViewHolder {
        public SquareImageView imageView;

        public ExploreProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.exploreImageViewHolder);
        }
    }
}
