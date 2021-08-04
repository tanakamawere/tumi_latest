package org.tmz.tumi.Main.Explore;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.tmz.tumi.Adapters.TagsAdapter;
import org.tmz.tumi.Objects.BusinessObject;
import org.tmz.tumi.Objects.StockMainObject;
import org.tmz.tumi.Pictures.PictureActivity;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FilePaths;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.SquareImageView;
import org.tmz.tumi.Utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FragmentCreateAd extends Fragment {

    private static final String TAG = "FragmentCreateAd";
    private final String mAppend = "file:/";
    public FirebaseMethods firebaseMethods;
    public BusinessObject business;
    //Widgets
    private TextView title, price,
            businessName, businessPhone, businessAddress, instruction;
    private ImageButton backButton;
    private TextInputEditText optionalDescription, tags;
    private SquareImageView imageView;
    private DatabaseReference databaseReference;
    private FrameLayout openGallery;
    private LinearLayout linearLayout1, linearLayout2;
    private Button doneButton;
    private Spinner category;
    //Val
    private String stockSelected;
    private String imageSelected;
    private String adKey;
    private String adLocation;
    private String fromWhere;
    private Bitmap bitmapSelected;
    private Bundle bundle;
    private ProgressDialog dialog;
    private DatabaseReference database;
    private FilePaths filePaths;
    private ArrayList<String> tagsArray;
    private StringBuilder builder;

    //RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_ad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tagsArray = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerViewTagsCreateAd);

        linearLayout1 = view.findViewById(R.id.linearLayoutCreateAd);
        linearLayout2 = view.findViewById(R.id.linearLayoutCreateAdTwo);

        dialog = new ProgressDialog(requireActivity());
        dialog.setTitle("Uploading");
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        //Database reference
        database = FirebaseDatabase.getInstance().getReference();
        firebaseMethods = new FirebaseMethods();

        //Title of the picture is the advertisement key
        adKey = database.child("Advertisements").push().getKey();

        //Editing File Paths for picture upload
        filePaths = new FilePaths();
        adLocation = filePaths.ADVERTISEMENT_STORAGE_LOCATION + "/" + adKey;

        //FrameLayout to open PictureActivity
        //Loading image into the ImageView
        openGallery = view.findViewById(R.id.advertisedOpenGallery);
        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), PictureActivity.class);
                intent.putExtra(getString(R.string.comingFromWhere), getString(R.string.comingFromAd));
                intent.putExtra(getString(R.string.selectedStockExtra), stockSelected);
                startActivity(intent);
            }
        });

        //TextViewButton
        doneButton = view.findViewById(R.id.doneCreateTextView);

        //EditText
        optionalDescription = view.findViewById(R.id.advertisedDescriptionCreateEditText);
        tags = view.findViewById(R.id.advertisedTagCreateEditText);
        //Initializing TextViews
        title = view.findViewById(R.id.advertisedCreateTitleTextView);
        price = view.findViewById(R.id.advertisedCreatePriceTextView);
        businessName = view.findViewById(R.id.advertisedCreateBusinessNameTextView);
        businessPhone = view.findViewById(R.id.advertisedCreatePhoneTextView);
        businessAddress = view.findViewById(R.id.advertisedCreateAddressTextView);
        instruction = view.findViewById(R.id.instructionCreateTextView);
        //ImageView
        imageView = view.findViewById(R.id.advertisedCreatePicture);

        //ImageButton initialization
        backButton = view.findViewById(R.id.advertisedCreateBackButton);
        //Category auto complete text view
        category = view.findViewById(R.id.advertisedCategoryAutoText);
        String[] categoryArray = {"Catering",
                "Cleaning",
                "Hairdressing",
                "Photography & Media",
                "Printing",
                "Tech Accessories",
                "Food & Drink",
                "Clothing",
                "Medicine",
                "General Products",
                "Rentals",
                "Books",
                "Beauty Products",
                "Cleaning Supplies",
                "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(requireActivity(), R.layout.spinner_item, categoryArray);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_item);
        category.setAdapter(categoryAdapter);

        //Getting bundle extras
        bundle = getArguments();
        assert bundle != null;
        stockSelected = bundle.getString(requireActivity().getString(R.string.selectedStockAd));
        fromWhere = bundle.getString(getString(R.string.fromWhere));

        if (fromWhere.equals(getString(R.string.stockAdvertise))) {
            linearLayout1.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.GONE);
        } else if (fromWhere.equals(getString(R.string.selectedPictureAdvertise))) {
            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
            instruction.setVisibility(View.GONE);
        }

        try {
            bitmapSelected = bundle.getParcelable(getString(R.string.selectedBitmap));
            imageSelected = bundle.getString(getString(R.string.selectedImage));
        } catch (Exception e) {
            Log.e(TAG, "onViewCreated: Getting a null image: " + e.getMessage());
            Toast.makeText(requireActivity(), "Select an image for the product", Toast.LENGTH_SHORT).show();
        }

        //Adding tags when (,) is added
        tags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(",")) {
                    //Formatting the string
                    String formattedString = s.toString().replace(",", "");
                    Log.e(TAG, "onTextChanged: Let's see if tag is being added: " + formattedString);
                    tagsArray.add(formattedString);
                    adapter.notifyDataSetChanged();
                    //Converting arrayList to string
                    builder = new StringBuilder();
                    for (String string : tagsArray) {
                        builder.append(string);
                        builder.append("#");
                    }
                    Log.e(TAG, "onTextChanged: ArrayList String: " + builder.toString());
                    Objects.requireNonNull(tags.getText()).clear();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //On click listener

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: I am pressing the done button");
                uploadAdvertisementPicture();
            }
        });


        //Init imageLoader
        initImageLoader();
        getTags();
        populatePage();
        setPicture();
    }

    private void getTags() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TagsAdapter(tagsArray);
        recyclerView.setAdapter(adapter);
    }

    private void setPicture() {
        Log.e(TAG, "setPicture: " + imageSelected);

        if (bitmapSelected != null) {
            imageView.setImageBitmap(bitmapSelected);
        } else if (imageSelected != null) {
            UniversalImageLoader.setImage(imageSelected, imageView, null, mAppend);
        }
    }

    private void uploadAdvertisementPicture() {
        if (bitmapSelected == null && imageSelected == null) {
            Toast.makeText(requireActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
        } else {
            if (bitmapSelected != null) {
                firebaseMethods.uploadingPhoto(requireActivity(),
                        null,
                        bitmapSelected,
                        dialog,
                        adLocation,
                        1,
                        adKey);
            } else if (imageSelected != null) {
                firebaseMethods.uploadingPhoto(requireActivity(),
                        imageSelected,
                        null,
                        dialog,
                        adLocation,
                        1,
                        adKey);
            }
            //Now upload other information to database
            uploadAdvertisementDetails();
        }
    }

    private void uploadAdvertisementDetails() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("adProductTitle", title.getText().toString());
        hashMap.put("adProductPrice", price.getText().toString());
        hashMap.put("adProductDescription", optionalDescription.getText().toString());
        hashMap.put("adKey", adKey);
        hashMap.put("adVertiserKey", firebaseMethods.getUserID());
        hashMap.put("adBusinessName", businessName.getText().toString());
        hashMap.put("adBusinessPhone", businessPhone.getText().toString());
        hashMap.put("adBusinessAddress", businessAddress.getText().toString());
        hashMap.put("adTags", builder.toString());
        hashMap.put("adCategory", category.getSelectedItem().toString());

        firebaseMethods.getDatabaseReference().child(getString(R.string.fbExplore))
                .child(getString(R.string.fbAdvertisements)).child(adKey).updateChildren(hashMap);
        FirebaseDatabase.getInstance().getReference().child(getString(R.string.fbAdvertisements))
                .child(adKey).updateChildren(hashMap);
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(requireActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void populatePage() {
        databaseReference = firebaseMethods.getDatabaseReference();

        databaseReference
                .child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbStock))
                .child(stockSelected).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    StockMainObject stockMainObject = snapshot.getValue(StockMainObject.class);
                    assert stockMainObject != null;
                    title.setText(stockMainObject.getProductTitle());
                    price.setText(getString(R.string.currencyIdentifier) + stockMainObject.getProductSellingPrice() + " each");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //Getting the advertiser details
        databaseReference
                .child(getString(R.string.fbBusinessInfo)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    business = snapshot.getValue(BusinessObject.class);
                    assert business != null;

                    businessName.setText(business.getName());
                    businessAddress.setText(business.getAddress());
                    businessPhone.setText(business.getPhoneNumber());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
