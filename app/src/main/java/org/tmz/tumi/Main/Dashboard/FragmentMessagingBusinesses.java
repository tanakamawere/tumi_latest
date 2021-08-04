package org.tmz.tumi.Main.Dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

import org.tmz.tumi.Adapters.SBMAdapter;
import org.tmz.tumi.Objects.BusinessObject;
import org.tmz.tumi.R;

import java.util.ArrayList;

public class FragmentMessagingBusinesses extends Fragment {
    private static final String TAG = "FragmentSearchBusiness";

    private RecyclerView recyclerView;
    private TextView nullBusinessTextView;
    private ProgressBar progressBar;
    private SBMAdapter businessAdapter;
    private ArrayList<BusinessObject> businessObjects;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messaging_businesses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nullBusinessTextView = view.findViewById(R.id.nullSBMTextView);
        nullBusinessTextView.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.recyclerViewSBM);
        progressBar = view.findViewById(R.id.progressBarSBM);
        businessObjects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        initializingBusinessRecyclerView();
    }

    private void initializingBusinessRecyclerView() {
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

        databaseReference.child(getString(R.string.fbBusinesses)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = "",
                            type = "",
                            category = "",
                            key = "",
                            picture = "";

                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        name = childSnap.child("Name").getValue().toString();
                        type = childSnap.child("Type").getValue().toString();
                        category = childSnap.child("Category").getValue().toString();
                        key = childSnap.child("Key").getValue().toString();
                        picture = childSnap.child("profilePicture").getValue().toString();

                        BusinessObject object = new BusinessObject(null, null, name, null, type, category, picture, key, null);
                        businessObjects.add(object);
                        businessAdapter.notifyDataSetChanged();
                    }
                } else {
                    nullBusinessTextView.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        businessAdapter = new SBMAdapter(businessObjects, requireActivity());
        recyclerView.setAdapter(businessAdapter);
    }
}
