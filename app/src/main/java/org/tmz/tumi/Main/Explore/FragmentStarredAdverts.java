package org.tmz.tumi.Main.Explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.StarredAdvertAdapter;
import org.tmz.tumi.Objects.AdvertisementsObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.util.ArrayList;

public class FragmentStarredAdverts extends Fragment {
    private static final String TAG = "FragmentStarredAdverts";

    public FirebaseMethods firebaseMethods;
    private RecyclerView recyclerViewAds;
    private RecyclerView.Adapter adapterAds;
    private RecyclerView.LayoutManager layoutManagerAds;
    private ArrayList<AdvertisementsObject> arrayListAds;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_starred_adverts, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewAds = view.findViewById(R.id.recyclerViewStarredAds);
        layoutManagerAds = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        arrayListAds = new ArrayList<>();
        firebaseMethods = new FirebaseMethods();
        databaseReference = firebaseMethods.getDatabaseReference();

        initializeRecyclerViewAds();
    }

    private void initializeRecyclerViewAds() {
        recyclerViewAds.setHasFixedSize(false);
        recyclerViewAds.setNestedScrollingEnabled(false);

        recyclerViewAds.setLayoutManager(layoutManagerAds);

        databaseReference.child("Explore").child(getString(R.string.fbStarredAdvertisements))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot childSnap : snapshot.getChildren()) {
                                String title = "",
                                        imageURL = "",
                                        key = "";

                                try {
                                    title = childSnap.child("adProductTitle").getValue().toString();
                                    key = childSnap.child("adKey").getValue().toString();
                                    imageURL = childSnap.child("imageURL").getValue().toString();
                                } catch (NullPointerException e) {
                                    Log.e(TAG, "onDataChange: Failed to take child: " + e.toString());
                                }

                                AdvertisementsObject object =
                                        new AdvertisementsObject(title, null, null, key, null, null, null, imageURL, null, null, null);
                                arrayListAds.add(object);
                                adapterAds.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        adapterAds = new StarredAdvertAdapter(arrayListAds);
        recyclerViewAds.setAdapter(adapterAds);
    }
}
