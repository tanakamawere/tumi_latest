package org.tmz.tumi.Main.Explore;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.tmz.tumi.Adapters.ExploreBusinessAdapter;
import org.tmz.tumi.Adapters.ExploreProductsAdapter;
import org.tmz.tumi.Adapters.RequestsPublicAdapter;
import org.tmz.tumi.Objects.AdvertisementsObject;
import org.tmz.tumi.Objects.BusinessObject;
import org.tmz.tumi.Objects.RequestObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class FragmentSearch extends Fragment {
    private static final String TAG = "FragmentSearch";

    private static final int NUMBER_GRID_COLUMNS = 3;
    private RecyclerView recyclerView, recyclerViewBusiness, recyclerViewServices, recyclerViewRequests;
    private ExploreProductsAdapter adapter;
    private ExploreBusinessAdapter businessAdapter;
    private RequestsPublicAdapter requestsPublicAdapter;
    //Arrays
    private ArrayList<AdvertisementsObject> photo;
    private ArrayList<BusinessObject> businessObjects;
    private ArrayList<RequestObject> requestObjects;

    private DatabaseReference databaseReference;
    private FirebaseMethods firebaseMethods;

    //Views
    private LinearLayout linearLayoutServices, linearLayoutProducts, linearLayoutBusinesses, linearLayoutRequests;
    private Chip chip;
    private ChipGroup chipGroup;
    private ImageButton exitSearchButton, clearEditTextButton;
    private TextInputEditText searchEditText;
    private TextView nullSearch;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseMethods = new FirebaseMethods();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Arrays
        photo = new ArrayList<>();
        businessObjects = new ArrayList<>();
        requestObjects = new ArrayList<>();

        clearEditTextButton = view.findViewById(R.id.clearSearchButton);
        exitSearchButton = view.findViewById(R.id.exitSearchButton);
        searchEditText = view.findViewById(R.id.searchMainEditText);
        recyclerView = view.findViewById(R.id.recyclerViewSearchProducts);
        recyclerViewBusiness = view.findViewById(R.id.recyclerViewSearchBusinesses);
        recyclerViewServices = view.findViewById(R.id.recyclerViewSearchServices);
        recyclerViewRequests = view.findViewById(R.id.recyclerViewSearchRequests);
        progressBar = view.findViewById(R.id.progressBarSearch);
        nullSearch = view.findViewById(R.id.nullSearchTextView);
        chipGroup = view.findViewById(R.id.chipGroupSearch);

        linearLayoutBusinesses = view.findViewById(R.id.linearLayoutSearchBusinesses);
        linearLayoutProducts = view.findViewById(R.id.linearLayoutSearchProducts);
        linearLayoutServices = view.findViewById(R.id.linearLayoutSearchServices);
        linearLayoutRequests = view.findViewById(R.id.linearLayoutSearchRequests);

        linearLayoutBusinesses.setVisibility(View.GONE);
        linearLayoutProducts.setVisibility(View.GONE);
        linearLayoutServices.setVisibility(View.GONE);
        linearLayoutRequests.setVisibility(View.GONE);
        nullSearch.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        //Chips
        //Showing the visible checked chip
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                chip = view.findViewById(checkedId);
                if (chip != null) {
                    if (chip.getText().toString().equals(getString(R.string.searchAll))) {
                        linearLayoutBusinesses.setVisibility(View.VISIBLE);
                        linearLayoutProducts.setVisibility(View.VISIBLE);
                        linearLayoutServices.setVisibility(View.VISIBLE);
                        linearLayoutRequests.setVisibility(View.VISIBLE);
                    } else if (chip.getText().toString().equals(getString(R.string.products))) {
                        linearLayoutProducts.setVisibility(View.VISIBLE);
                        linearLayoutBusinesses.setVisibility(View.GONE);
                        linearLayoutServices.setVisibility(View.GONE);
                        linearLayoutRequests.setVisibility(View.GONE);
                    } else if (chip.getText().toString().equals(getString(R.string.services))) {
                        linearLayoutProducts.setVisibility(View.GONE);
                        linearLayoutBusinesses.setVisibility(View.GONE);
                        linearLayoutRequests.setVisibility(View.GONE);
                        linearLayoutServices.setVisibility(View.VISIBLE);
                    } else if (chip.getText().toString().equals(getString(R.string.businesses))) {
                        linearLayoutProducts.setVisibility(View.GONE);
                        linearLayoutBusinesses.setVisibility(View.VISIBLE);
                        linearLayoutServices.setVisibility(View.GONE);
                        linearLayoutRequests.setVisibility(View.GONE);
                    } else if (chip.getText().toString().equals(getString(R.string.requests))) {
                        linearLayoutProducts.setVisibility(View.GONE);
                        linearLayoutBusinesses.setVisibility(View.GONE);
                        linearLayoutServices.setVisibility(View.GONE);
                        linearLayoutRequests.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        //Views
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterAdRecyclerView(s.toString());
                filterBusinessRecyclerView(s.toString());
            }
        });
        nullSearch.setText(String.format("Nothing found for %s", searchEditText.getText().toString()));

        initializeSearchFunction();
        initializingBusinessRecyclerView();
        initializingAdRecyclerView();
        initImageLoader();
    }

    private void initializeSearchFunction() {
        clearEditTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(searchEditText.getText()).clear();
            }
        });

        exitSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(requireActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void initializingAdRecyclerView() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), NUMBER_GRID_COLUMNS));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("Advertisements");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String URL = "",
                            adKey = "",
                            adProductTitle = "",
                            adProductDescription = "",
                            adCategory = "";
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        //photo.add(snapshot1.getValue(AdvertisementsObject.class));

                        try {
                            URL = snapshot1.child("imageURL").getValue().toString();
                            adKey = snapshot1.child("adKey").getValue().toString();
                            adProductTitle = snapshot1.child("adProductTitle").getValue().toString();
                            adProductDescription = snapshot1.child("adProductDescription").getValue().toString();
                            adCategory = snapshot1.child("adCategory").getValue().toString();
                        } catch (NullPointerException e) {
                            Log.e(TAG, "onDataChange: Failed to retrieve a child: " + e.toString());
                        }


                        AdvertisementsObject object = new AdvertisementsObject(adProductTitle, null, adProductDescription, adKey, null, null, null, URL, null, null, adCategory);
                        photo.add(object);
                        Collections.shuffle(photo);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    nullSearch.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        adapter = new ExploreProductsAdapter(photo, requireActivity());
        recyclerView.setAdapter(adapter);
    }

    private void initializingBusinessRecyclerView() {
        recyclerViewBusiness.setHasFixedSize(false);
        recyclerViewBusiness.setNestedScrollingEnabled(false);
        recyclerViewBusiness.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));

        databaseReference.child(getString(R.string.fbBusinesses))
                .addValueEventListener(new ValueEventListener() {
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
                            nullSearch.setVisibility(View.VISIBLE);
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        businessAdapter = new ExploreBusinessAdapter(requireActivity(), businessObjects);
        recyclerViewBusiness.setAdapter(businessAdapter);
    }

    private void initializeRequestRecyclerView() {
        recyclerViewRequests.setHasFixedSize(false);
        recyclerViewRequests.setNestedScrollingEnabled(false);
        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));

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
                        requestObjects.add(storyObject);
                        requestsPublicAdapter.notifyDataSetChanged();
                    }
                } else {
                    nullSearch.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        requestsPublicAdapter = new RequestsPublicAdapter(requestObjects);
        recyclerView.setAdapter(requestsPublicAdapter);
    }

    private void filterRequestsRecyclerView(String text) {
        ArrayList<RequestObject> searchedRequests
                = new ArrayList<>();

        for (RequestObject object : requestObjects) {
            if (object.getStoryTitle().toLowerCase().contains(text.toLowerCase())) {
                searchedRequests.add(object);
            }
            if (object.getStoryDescription().toLowerCase().contains(text.toLowerCase())) {
                searchedRequests.add(object);
            }
            if (object.getStoryBody().toLowerCase().contains(text.toLowerCase())) {
                searchedRequests.add(object);
            }
        }

        requestsPublicAdapter.filterList(searchedRequests);
    }

    private void filterAdRecyclerView(String text) {
        ArrayList<AdvertisementsObject> searchedAdList
                = new ArrayList<>();

        for (AdvertisementsObject object : photo) {
            if (object.getAdProductTitle().toLowerCase().contains(text.toLowerCase())) {
                searchedAdList.add(object);
            }
            if (object.getAdProductDescription().toLowerCase().contains(text.toLowerCase())) {
                searchedAdList.add(object);
            }
            if (object.getAdCategory().toLowerCase().contains(text.toLowerCase())) {
                searchedAdList.add(object);
            }
        }

        adapter.filterList(searchedAdList);
    }

    private void filterBusinessRecyclerView(String text) {
        ArrayList<BusinessObject> searchedList
                = new ArrayList<>();

        for (BusinessObject object : businessObjects) {
            if (object.getName().toLowerCase().contains(text.toLowerCase())) {
                searchedList.add(object);
            }
            if (object.getCategory().toLowerCase().contains(text.toLowerCase())) {
                searchedList.add(object);
            }
        }

        businessAdapter.filterList(searchedList);
    }
}
