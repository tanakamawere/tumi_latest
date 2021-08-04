package org.tmz.tumi.Main.Explore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.tmz.tumi.Adapters.ExploreProductsAdapter;
import org.tmz.tumi.Main.Dashboard.FragmentSendMessage;
import org.tmz.tumi.Objects.AdvertisementsObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentViewBusiness extends Fragment {

    private static final String TAG = "FragmentViewBusiness";
    private static final int NUMBER_GRID_COLUMNS = 3;
    private RecyclerView recyclerView;
    private ExploreProductsAdapter adapter;
    private ArrayList<AdvertisementsObject> photo;
    private ProgressBar progressBar;
    private TextView name, businessCategory, email, phone, address, description, fragmentTitle;
    private DatabaseReference databaseReference;
    private String selectedBusiness, fromWhere;
    private ImageView imageView;
    private ImageButton back, message, call;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_business, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Receiving Bundle
        Bundle bundle = getArguments();
        assert bundle != null;
        selectedBusiness = bundle.getString(getString(R.string.selectedBusiness));
        fromWhere = bundle.getString(getString(R.string.fromWhere));

        //Widgets initializations
        recyclerView = view.findViewById(R.id.recyclerViewViewBusiness);
        progressBar = view.findViewById(R.id.progressBarViewBusiness);
        photo = new ArrayList<>();
        imageView = view.findViewById(R.id.viewBusinessPicture);

        //TextViews
        name = view.findViewById(R.id.viewBusinessNameTextView);
        businessCategory = view.findViewById(R.id.viewBusinessCategoryTextView);
        email = view.findViewById(R.id.viewBusinessEmailTextView);
        phone = view.findViewById(R.id.viewBusinessPhoneTextView);
        address = view.findViewById(R.id.viewBusinessAddressTextView);
        description = view.findViewById(R.id.viewBusinessDescriptionTextView);
        fragmentTitle = view.findViewById(R.id.viewBusinessTitle);

        //ImageButtons
        back = view.findViewById(R.id.businessViewBackButton);
        call = view.findViewById(R.id.viewBusinessCallButton);
        message = view.findViewById(R.id.viewBusinessMessageBusiness);

        //Database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        initImageLoader();
        populatePage();
        onClickListeners();
        initializingRecyclerView();
    }

    private void onClickListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone.getText().toString()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onClick: Opening Call Dialog failed: " + e.getMessage());
                }
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Method to open WhatsApp
                /*try {
                    Intent intentApp = new Intent(Intent.ACTION_VIEW);
                    String URL = "https://api.message.com/send?phone=" + phone.getText().toString();
                    intentApp.setData(Uri.parse(URL));
                    startActivity(intentApp);
                } catch (Exception e) {
                    Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onClick: Opening WhatsApp failed: " + e.getMessage());
                }*/
                Fragment fragment = new FragmentSendMessage();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.selectedBusiness), selectedBusiness);
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                if (fromWhere.equals(getString(R.string.dashboardActivity))) {
                    fragmentManager.beginTransaction().setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_out).replace(R.id.frameLayoutDashboard, fragment)
                            .addToBackStack(null)
                            .commit();
                } else if (fromWhere.equals(getString(R.string.exploreActivity))) {
                    fragmentManager.beginTransaction().setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_out).replace(R.id.frameLayoutExplore, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(requireActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void populatePage() {
        //Getting the user's profile picture
        databaseReference.child(getString(R.string.fbUsers))
                .child(selectedBusiness).child(getString(R.string.fbUserInfo)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UniversalImageLoader.setImage(snapshot.child("profilePicture").getValue().toString(), imageView, null, "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //Getting the details from the user's personal node
        databaseReference.child(getString(R.string.fbUsers))
                .child(selectedBusiness).child(getString(R.string.fbBusinessInfo)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    fragmentTitle.setText(snapshot.child("Name").getValue().toString());
                    name.setText(snapshot.child("Name").getValue().toString());
                    businessCategory.setText(snapshot.child("Category").getValue().toString());
                    email.setText(snapshot.child("EmailAddress").getValue().toString());
                    phone.setText(snapshot.child("PhoneNumber").getValue().toString());
                    address.setText(snapshot.child("Address").getValue().toString());
                    description.setText(snapshot.child("Description").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initializingRecyclerView() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), NUMBER_GRID_COLUMNS));


        DatabaseReference mDB = FirebaseDatabase.getInstance().getReference();
        mDB.child(getString(R.string.fbAdvertisements)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String URL = "",
                            adKey = "",
                            adProductTitle = "",
                            adVertiserKey = "";
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        //photo.add(snapshot1.getValue(AdvertisementsObject.class));
                        try {
                            URL = snapshot1.child("imageURL").getValue().toString();
                            adKey = snapshot1.child("adKey").getValue().toString();
                            adProductTitle = snapshot1.child("adProductTitle").getValue().toString();
                            adVertiserKey = snapshot1.child("adVertiserKey").getValue().toString();
                        } catch (NullPointerException e) {
                            Log.e(TAG, "onDataChange: Failed to take child: " + e.toString());
                        }

                        if (adVertiserKey.equals(new FirebaseMethods().getUserID())) {
                            AdvertisementsObject object = new AdvertisementsObject(adProductTitle, null, null, adKey, null, null, null, URL, null, null, null);
                            photo.add(object);
                            Collections.shuffle(photo);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Toast.makeText(requireActivity(), "Business has no advertised products", Toast.LENGTH_SHORT).show();
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
}
