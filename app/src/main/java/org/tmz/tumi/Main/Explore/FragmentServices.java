package org.tmz.tumi.Main.Explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.tmz.tumi.Adapters.ServicesAdapter;
import org.tmz.tumi.Objects.ServicesObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentServices extends Fragment {

    private static final String TAG = "FragmentServices";
    //Strings for Services DataSnapshot
    String name = "",
            type = "",
            category = "",
            key = "",
            picture = "",
            description = "";
    private RecyclerView recyclerViewBusiness;
    private ServicesAdapter servicesAdapter;
    private ArrayList<ServicesObject> servicesObjects;
    private DatabaseReference databaseReference;
    private TextView nullServicesTV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_services, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewBusiness = view.findViewById(R.id.recyclerViewExploreServices);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        servicesObjects = new ArrayList<>();
        nullServicesTV = view.findViewById(R.id.nullServicesTV);
        nullServicesTV.setVisibility(View.GONE);

        //Calling methods
        initImageLoader();
        initializingBusinessRecyclerView();
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(requireActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void initializingBusinessRecyclerView() {
        recyclerViewBusiness.setHasFixedSize(false);
        recyclerViewBusiness.setNestedScrollingEnabled(false);
        recyclerViewBusiness.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

        databaseReference.child(getString(R.string.fbServices)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        key = childSnap.child("Key").getValue().toString();

                        //Getting the details from the user's personal node
                        databaseReference.child(getString(R.string.fbUsers))
                                .child(key).child(getString(R.string.fbBusinessInfo)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    name = snapshot.child("Name").getValue().toString();
                                    type = snapshot.child("Type").getValue().toString();
                                    description = snapshot.child("Description").getValue().toString();
                                    picture = snapshot.child("profilePicture").getValue().toString();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                        Log.e(TAG, "onDataChange: Picture URL: " + picture);
                        ServicesObject object = new ServicesObject(name, type, picture, key, description, null);
                        Collections.shuffle(servicesObjects);
                        servicesObjects.add(object);
                        servicesAdapter.notifyDataSetChanged();
                    }
                } else {
                    nullServicesTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        servicesAdapter = new ServicesAdapter(servicesObjects, requireActivity());
        recyclerViewBusiness.setAdapter(servicesAdapter);
    }
}
