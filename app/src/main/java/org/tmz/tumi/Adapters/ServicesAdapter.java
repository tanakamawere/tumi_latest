package org.tmz.tumi.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.tmz.tumi.Main.Dashboard.FragmentSendMessage;
import org.tmz.tumi.Main.Explore.ExploreActivity;
import org.tmz.tumi.Main.Explore.FragmentServiceComment;
import org.tmz.tumi.Objects.ServicesObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.UniversalImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServicesViewHolder> {

    private final ArrayList<ServicesObject> arrayList;
    private final Context mContext;

    public ServicesAdapter(ArrayList<ServicesObject> arrayList, Context mContext) {
        this.arrayList = arrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_services, parent, false);
        return new ServicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ServicesViewHolder holder, int position) {
        final ServicesObject object = arrayList.get(position);

        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());

        UniversalImageLoader.setImage(object.getProfilePicture(), holder.circleImageView, null, "");
        holder.name.setText(object.getName());
        holder.service.setText(object.getType());
        holder.description.setText(object.getDescription());
        holder.rating.setText(object.getRating());
        holder.options.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            CharSequence[] options = new CharSequence[]{v.getContext().getString(R.string.sendMessage), v.getContext().getString(R.string.report)};
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    Fragment fragment = new FragmentSendMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString(v.getContext().getString(R.string.selectedBusiness), object.getKey());
                    fragment.setArguments(bundle);
                    ((ExploreActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .replace(R.id.frameLayoutExplore, fragment)
                            .addToBackStack(null)
                            .commit();
                } else if (which == 1) {
                    Toast.makeText(v.getContext(), "Reported", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        });

        holder.numberOfComments.setOnClickListener(v -> {
            Fragment fragment = new FragmentServiceComment();
            Bundle bundle = new Bundle();
            bundle.putString(v.getContext().getString(R.string.selectedService), object.getKey());
            fragment.setArguments(bundle);
            ((ExploreActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.frameLayoutExplore, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ServicesViewHolder extends RecyclerView.ViewHolder {
        public TextView name, service, description, rating, numberOfComments;
        public ImageButton options;
        public CircleImageView circleImageView;

        public ServicesViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.postVHProfilePicture);
            name = itemView.findViewById(R.id.servicesVHNameTextView);
            numberOfComments = itemView.findViewById(R.id.servicesVHCommentNumberTextView);
            service = itemView.findViewById(R.id.servicesVHServiceTextView);
            description = itemView.findViewById(R.id.servicesVHDescriptionTextView);
            rating = itemView.findViewById(R.id.servicesVHRatingTextView);
            options = itemView.findViewById(R.id.servicesVHOptionsButton);
        }
    }
}
