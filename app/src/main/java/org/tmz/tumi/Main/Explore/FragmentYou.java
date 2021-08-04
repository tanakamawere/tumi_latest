package org.tmz.tumi.Main.Explore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.tmz.tumi.Adapters.AdvertAdapter;
import org.tmz.tumi.Adapters.RequestsAdapter;
import org.tmz.tumi.Objects.AdvertisementsObject;
import org.tmz.tumi.Objects.RequestObject;
import org.tmz.tumi.Objects.User;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.UniversalImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentYou extends Fragment {
    private static final String TAG = "FragmentYou";
    private ImageButton backToExplore;
    private CircleImageView profilePicture;
    private FirebaseMethods firebaseMethods;
    private TextView toolbarTitleName;
    private RecyclerView recyclerView, recyclerViewAds;
    private RecyclerView.Adapter adapter, adapterAds;
    private RecyclerView.LayoutManager layoutManager, layoutManagerAds;
    private DatabaseReference databaseReference;
    private ArrayList<RequestObject> arrayList;
    private ArrayList<AdvertisementsObject> arrayListAds;
    private TextView nullDetailsTextView;
    private Button starredAdsButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_you, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profilePicture = view.findViewById(R.id.youProfilePicture);
        firebaseMethods = new FirebaseMethods();
        toolbarTitleName = view.findViewById(R.id.youToolbarName);
        starredAdsButton = view.findViewById(R.id.starredAdsButton);

        backToExplore = view.findViewById(R.id.youBackButton);
        backToExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), ExploreActivity.class));
            }
        });
        starredAdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.frameLayoutExplore, new FragmentStarredAdverts())
                        .addToBackStack(null).commit();
            }
        });

        nullDetailsTextView = view.findViewById(R.id.nothingToShowYouTextView);

        recyclerView = view.findViewById(R.id.recyclerViewUserRequests);
        recyclerViewAds = view.findViewById(R.id.recyclerViewUserAds);

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManagerAds = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        arrayList = new ArrayList<>();
        arrayListAds = new ArrayList<>();

        firebaseMethods = new FirebaseMethods();
        databaseReference = firebaseMethods.getDatabaseReference();

        initializeRecyclerView();
        initializeRecyclerViewAds();
        initImageLoader();
        populateHeader();
    }

    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out  // popExit
        ).replace(R.id.frameLayoutExplore, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(requireActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void initializeRecyclerViewAds() {
        recyclerViewAds.setHasFixedSize(false);
        recyclerViewAds.setNestedScrollingEnabled(false);

        recyclerViewAds.setLayoutManager(layoutManagerAds);

        databaseReference.child(getString(R.string.fbExplore)).child(getString(R.string.fbAdvertisements))
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
                                    imageURL = childSnap.child("imageURL").getValue().toString();
                                    key = childSnap.child("adKey").getValue().toString();
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
        adapterAds = new AdvertAdapter(arrayListAds);
        recyclerViewAds.setAdapter(adapterAds);
    }

    private void initializeRecyclerView() {
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(layoutManager);

        databaseReference
                .child(getString(R.string.fbExplore)).child("Stories").addValueEventListener(new ValueEventListener() {
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new RequestsAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }

    private void populateHeader() {
        DatabaseReference databaseReference1 = firebaseMethods.getDatabaseReference();
        databaseReference1.child(getString(R.string.fbUserInfo)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                toolbarTitleName.setText(user.getFullName());
                String imgURL = user.getProfilePicture();
                UniversalImageLoader.setImage(imgURL, profilePicture, null, "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
