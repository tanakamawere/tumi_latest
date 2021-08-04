package org.tmz.tumi.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.tmz.tumi.Main.Dashboard.DashboardActivity;
import org.tmz.tumi.Main.Dashboard.FragmentSendMessage;
import org.tmz.tumi.Main.Explore.FragmentViewBusiness;
import org.tmz.tumi.Objects.BusinessObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.UniversalImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SBMAdapter extends RecyclerView.Adapter<SBMAdapter.SBMViewHolder> {
    private static final String TAG = "SBMAdapter";

    private final ArrayList<BusinessObject> arrayList;
    private final Context mContext;

    public SBMAdapter(ArrayList<BusinessObject> arrayList, Context context) {
        this.arrayList = arrayList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public SBMViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_search_business_messages, parent, false);
        return new SBMViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SBMViewHolder holder, int position) {
        final BusinessObject object = arrayList.get(position);
        final String selectedBusiness = object.getKey();

        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());

        holder.name.setText(object.getName());
        holder.category.setText(object.getCategory());
        UniversalImageLoader.setImage(object.getProfilePicture(), holder.profilePicture, null, "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onItemClick: Selected a position -- Key: " + object.getKey());

                Fragment fragment = new FragmentViewBusiness();
                Bundle bundle = new Bundle();
                bundle.putString(mContext.getString(R.string.selectedBusiness), selectedBusiness);
                bundle.putString(mContext.getString(R.string.fromWhere), mContext.getString(R.string.dashboardActivity));
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = ((DashboardActivity) v.getContext()).getSupportFragmentManager();

                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out  // popExit
                        )
                        .replace(R.id.frameLayoutDashboard, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        holder.messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FragmentSendMessage();
                Bundle bundle = new Bundle();
                bundle.putString(mContext.getString(R.string.selectedBusiness), selectedBusiness);
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = ((DashboardActivity) v.getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out).replace(R.id.frameLayoutDashboard, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class SBMViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView profilePicture;
        private final TextView name;
        private final TextView category;
        private final ImageButton messageButton;

        public SBMViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.SBMBusinessNameTextView);
            category = itemView.findViewById(R.id.SBMBusinessCategoryTextView);
            profilePicture = itemView.findViewById(R.id.SBMImageView);
            messageButton = itemView.findViewById(R.id.SBMQuickMessage);
        }
    }
}
