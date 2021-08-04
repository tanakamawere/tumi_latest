package org.tmz.tumi.Main.Explore;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.tmz.tumi.Adapters.ExploreProductsAdapter;
import org.tmz.tumi.Adapters.TagsViewAdAdapter;
import org.tmz.tumi.Main.Dashboard.FragmentSendMessage;
import org.tmz.tumi.Objects.AdvertisementsObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.Miscellaneous;
import org.tmz.tumi.Utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FragmentViewAd extends Fragment {

    private static final String TAG = "FragmentViewAd";
    private final int NUMBER_GRID_COLUMNS = 3;
    //Widgets
    private TextView title, price, businessName, businessPhone, nullSimilarPosts,
            businessAddress, description, fragmentTitle, category;
    private ImageButton back, callButton, starProductButton, messageBusiness, viewBusiness;
    private ImageView imageView;
    private ScrollView container;
    private ProgressBar progressBar, pgPicture;
    //Variables
    private String selectedProductKey, selectedProductImageURL;
    private DatabaseReference databaseReference;
    //RecyclerViews: Tags
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private RecyclerView recyclerViewSimilar;
    private RecyclerView.Adapter adapterSimilar;
    private ArrayList<AdvertisementsObject> photo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_ad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = getArguments();
        assert bundle != null;
        selectedProductKey = bundle.getString(getString(R.string.selectedProductGridView));

        recyclerView = view.findViewById(R.id.recyclerViewTagsViewAd);
        recyclerViewSimilar = view.findViewById(R.id.recyclerViewSimilarProducts);
        photo = new ArrayList<>();
        //Initialising Container and progressBar
        container = view.findViewById(R.id.containerViewAd);
        container.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progressBarViewAd);
        pgPicture = view.findViewById(R.id.progressBarViewAdPicture);
        //Initializing TextViews
        title = view.findViewById(R.id.advertisedTitleTextView);
        price = view.findViewById(R.id.advertisedPriceTextView);
        businessName = view.findViewById(R.id.advertisedBusinessNameTextView);
        businessPhone = view.findViewById(R.id.advertisedPhoneTextView);
        businessAddress = view.findViewById(R.id.advertisedAddressTextView);
        description = view.findViewById(R.id.advertisedDescriptionTextView);
        category = view.findViewById(R.id.advertisedCategoryTextView);
        nullSimilarPosts = view.findViewById(R.id.similarProductsNullTV);
        nullSimilarPosts.setVisibility(View.GONE);
        //ImageView
        imageView = view.findViewById(R.id.advertisedPicture);

        //ImageButton initialization
        starProductButton = view.findViewById(R.id.starProductButton);
        viewBusiness = view.findViewById(R.id.viewBusinessFromProductButton);
        //Buttons
        callButton = view.findViewById(R.id.advertisedCallButton);
        messageBusiness = view.findViewById(R.id.viewAdMessageBusiness);

        //Init imageLoader
        initImageLoader();
        populatePage();

        //on click listeners
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        starProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMethods firebaseMethods = new FirebaseMethods();
                DatabaseReference database = firebaseMethods.getDatabaseReference();

                HashMap<String, Object> map = new HashMap<>();
                map.put("adKey", selectedProductKey);
                map.put("adProductTitle", title.getText().toString());
                Log.e(TAG, "onClick: ImageURL obtained: " + selectedProductImageURL);
                map.put("imageURL", selectedProductImageURL);

                database.child(getString(R.string.fbExplore)).child(getString(R.string.fbStarredAdvertisements))
                        .child(selectedProductKey).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(requireActivity(), "Starring successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireActivity(), "Starring failed. Try again later", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(requireActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void populatePage() {
        final Miscellaneous misc = new Miscellaneous();
        final DatabaseReference nDB = new FirebaseMethods().getDatabaseReference()
                .child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbNotifications));

        databaseReference.child(getString(R.string.fbAdvertisements)).child(selectedProductKey)
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final AdvertisementsObject object = snapshot.getValue(AdvertisementsObject.class);
                        assert object != null;
                        if (object.getAdProductDescription().equals(""))
                            description.setVisibility(View.GONE);

                        container.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        title.setText(object.getAdProductTitle());
                        fragmentTitle.setText(object.getAdProductTitle());
                        price.setText(object.getAdProductPrice());
                        description.setText(object.getAdProductDescription());
                        businessName.setText(object.getAdBusinessName());
                        businessPhone.setText(object.getAdBusinessPhone());
                        businessAddress.setText(object.getAdBusinessAddress());
                        selectedProductImageURL = object.getImageURL();
                        category.setText(object.getAdCategory());
                        //Calling the get similar products method here
                        initializingRecyclerViewSimilar(object.getAdCategory());

                        getTags(object.getAdTags());

                        viewBusiness.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Fragment fragment = new FragmentViewBusiness();
                                Bundle bundle = new Bundle();
                                bundle.putString(requireActivity().getString(R.string.selectedBusiness), object.getAdVertiserKey());
                                bundle.putString(getString(R.string.fromWhere), getString(R.string.exploreActivity));
                                fragment.setArguments(bundle);

                                FragmentManager fragmentManager = ((ExploreActivity) v.getContext()).getSupportFragmentManager();

                                fragmentManager.beginTransaction().setCustomAnimations(
                                        R.anim.slide_in,  // enter
                                        R.anim.fade_out,  // exit
                                        R.anim.fade_in,   // popEnter
                                        R.anim.slide_out  // popExit
                                ).replace(R.id.frameLayoutExplore, fragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });


                        callButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("title", object.getAdProductTitle());
                                    hashMap.put("quantity", "");
                                    hashMap.put("key", object.getAdKey());
                                    hashMap.put("date", misc.getDate());
                                    hashMap.put("time", misc.getTime());
                                    hashMap.put("description", getString(R.string.phoneNumberClicked));
                                    nDB.child(object.getAdKey()).updateChildren(hashMap);

                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + businessPhone.getText().toString()));
                                    startActivity(intent);
                                } catch (Exception e) {
                                    Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onClick: Opening Call Dialog failed: " + e.getMessage());
                                }
                            }
                        });

                        messageBusiness.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Fragment fragment = new FragmentSendMessage();
                                Bundle bundle = new Bundle();
                                bundle.putString(getString(R.string.selectedBusiness), object.getAdVertiserKey());
                                fragment.setArguments(bundle);
                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction().setCustomAnimations(
                                        R.anim.slide_in,  // enter
                                        R.anim.fade_out,  // exit
                                        R.anim.fade_in,   // popEnter
                                        R.anim.slide_out).replace(R.id.frameLayoutExplore, fragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });

                        UniversalImageLoader.setImage(object.getImageURL(), imageView, pgPicture, "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void initializingRecyclerViewSimilar(final String category) {
        //Method called from the populate page method

        recyclerViewSimilar.setNestedScrollingEnabled(false);
        recyclerViewSimilar.setHasFixedSize(false);
        recyclerViewSimilar.setLayoutManager(new GridLayoutManager(requireActivity(), NUMBER_GRID_COLUMNS));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child(getString(R.string.fbAdvertisements));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String URL = "",
                            adKey = "",
                            adProductTitle = "",
                            adCategory = "";
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        //photo.add(snapshot1.getValue(AdvertisementsObject.class));

                        try {
                            URL = snapshot1.child("imageURL").getValue().toString();
                            adKey = snapshot1.child("adKey").getValue().toString();
                            adProductTitle = snapshot1.child("adProductTitle").getValue().toString();
                            adCategory = snapshot1.child("adCategory").getValue().toString();
                        } catch (NullPointerException e) {
                            Log.e(TAG, "onDataChange: Failed to take child: " + e.toString());
                        }


                        if (category.equals(adCategory)) {
                            AdvertisementsObject object = new AdvertisementsObject(adProductTitle, null, null, adKey, null, null, null, URL, null, null, null);
                            photo.add(object);
                            Collections.shuffle(photo);
                            adapterSimilar.notifyDataSetChanged();
                        }
                    }
                } else {
                    nullSimilarPosts.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        adapterSimilar = new ExploreProductsAdapter(photo, requireActivity());
        recyclerViewSimilar.setAdapter(adapterSimilar);
    }

    private void getTags(String tagsFromDB) {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        String[] tagsStrings = tagsFromDB.split("#");
        List<String> tagsArray = new ArrayList<>(Arrays.asList(tagsStrings));

        adapter = new TagsViewAdAdapter(tagsArray);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
