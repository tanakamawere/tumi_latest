package org.tmz.tumi.Adapters;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import org.tmz.tumi.Main.Explore.ExploreActivity;
import org.tmz.tumi.Main.Explore.FragmentViewAd;
import org.tmz.tumi.Objects.AdvertisementsObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FilePaths;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.SquareImageView;
import org.tmz.tumi.Utils.UniversalImageLoader;

import java.util.ArrayList;

public class AdvertAdapter extends RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder> {

    private static final String TAG = "AdvertAdapter";
    public ArrayList<AdvertisementsObject> arrayList;
    private DatabaseReference global, user;

    public AdvertAdapter(ArrayList<AdvertisementsObject> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public AdvertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_user_ads, parent, false);
        return new AdvertAdapter.AdvertViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdvertViewHolder holder, int position) {
        final AdvertisementsObject object = arrayList.get(position);
        holder.title.setText(object.getAdProductTitle());

        UniversalImageLoader.setImage(object.getImageURL(), holder.squareImageView, null, "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                CharSequence[] options = new CharSequence[]{
                        "View Ad", "Delete"
                };
                builder.setTitle("What do you want to do?");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            Fragment fragment = new FragmentViewAd();
                            Bundle bundle = new Bundle();
                            bundle.putString(v.getContext().getResources().getString(R.string.selectedProductGridView), object.getAdKey());
                            fragment.setArguments(bundle);

                            FragmentManager fragmentManager = ((ExploreActivity) v.getContext()).getSupportFragmentManager();

                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(
                                            R.anim.slide_in,  // enter
                                            R.anim.fade_out,  // exit
                                            R.anim.fade_in,   // popEnter
                                            R.anim.slide_out  // popExit
                                    ).replace(R.id.frameLayoutExplore, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        } else if (which == 1) {
                            global = FirebaseDatabase.getInstance().getReference();

                            //Deleting image from firebase
                            FirebaseMethods firebaseMethods = new FirebaseMethods();
                            StorageReference storageReference = firebaseMethods.mStorageReference.child(new FilePaths().ADVERTISEMENT_STORAGE_LOCATION + "/" + object.getAdKey());
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e(TAG, "onSuccess: Picture deleted.");
                                }
                            });

                            //Deleting details
                            firebaseMethods.getDatabaseReference().child("Explore")
                                    .child("Advertisements").child(object.getAdKey()).removeValue();
                            global.child("Advertisements").child(object.getAdKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(v.getContext(), "Ad removed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class AdvertViewHolder extends RecyclerView.ViewHolder {

        public SquareImageView squareImageView;
        public TextView title;

        public AdvertViewHolder(@NonNull View itemView) {
            super(itemView);

            squareImageView = itemView.findViewById(R.id.viewHolderImage);
            title = itemView.findViewById(R.id.productTitleViewHolder);
        }
    }
}
