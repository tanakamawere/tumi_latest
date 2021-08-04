package org.tmz.tumi.Main.Explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.RequestsPublicAdapter;
import org.tmz.tumi.Objects.RequestObject;
import org.tmz.tumi.R;

import java.util.ArrayList;

public class FragmentRequests extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference databaseReference;
    private ArrayList<RequestObject> arrayList;
    private RequestsPublicAdapter adapter;

    private ProgressBar progressBar;

    //Widgets
    private ExtendedFloatingActionButton floatingActionButton;
    private ImageButton searchButton;
    private TextView nullRequests;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewExploreStory);
        arrayList = new ArrayList<>();

        progressBar = view.findViewById(R.id.progressBarPublicRequests);
        nullRequests = view.findViewById(R.id.nullPublicRequests);
        searchButton = view.findViewById(R.id.searchRequestsButton);
        floatingActionButton = view.findViewById(R.id.fabWriteRequest);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out).replace(R.id.frameLayoutExplore, new FragmentSearch())
                        .addToBackStack(null)
                        .commit();
            }
        });

        nullRequests.setVisibility(View.GONE);

        //OnClick listeners
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.frameLayoutExplore, new FragmentWriteRequest())
                        .addToBackStack(null)
                        .commit();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Stories");
        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        String title = "", description = "", body = "", key = "", date = "", time = "", writer = "", requesterUID = "", requesterPhone = "";

                        title = childSnap.child("storyTitle").getValue().toString();
                        description = childSnap.child("storyDescription").getValue().toString();
                        body = childSnap.child("storyBody").getValue().toString();
                        key = childSnap.child("storyKey").getValue().toString();
                        date = childSnap.child("storyDate").getValue().toString();
                        time = childSnap.child("storyTime").getValue().toString();
                        writer = childSnap.child("storyWriter").getValue().toString();
                        requesterUID = childSnap.child("requesterUID").getValue().toString();
                        requesterPhone = childSnap.child("requesterPhone").getValue().toString();

                        RequestObject storyObject = new RequestObject(requesterPhone, title, description, body, key, date, time, writer, requesterUID);
                        arrayList.add(storyObject);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    nullRequests.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new RequestsPublicAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }
}
