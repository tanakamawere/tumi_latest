package org.tmz.tumi.Finances.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import org.tmz.tumi.Adapters.ReceiptAdapter;
import org.tmz.tumi.Objects.BusinessObject;
import org.tmz.tumi.Objects.DisplayProductsObject;
import org.tmz.tumi.Objects.SaleDetailObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FilePaths;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.SquareImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FragmentCreateReceipt extends Fragment {

    private static final String TAG = "FragmentCreateReceipt";
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    ArrayList<DisplayProductsObject> arrayList;
    DatabaseReference databaseReference;
    //Widgets
    private TextView businessName, businessAddress, phone, email, date, total;
    private FrameLayout frameLayout;
    private SquareImageView imageView;
    private Button share;
    private ImageButton backButton;
    private CountryCodePicker codePicker;
    private EditText phoneNumber;
    //vars
    private String saleSelectedReceipt, phone1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_receipt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        assert bundle != null;
        saleSelectedReceipt = bundle.getString(getString(R.string.selectedSaleReceipt));

        businessName = view.findViewById(R.id.receiptBusinessNameTextView);
        businessAddress = view.findViewById(R.id.receiptBusinessAddressTextView);
        phone = view.findViewById(R.id.receiptBusinessPhoneTextView);
        email = view.findViewById(R.id.receiptBusinessEmailTextView);
        date = view.findViewById(R.id.receiptBusinessDateTextView);
        total = view.findViewById(R.id.receiptBusinessTotalTextView);
        backButton = view.findViewById(R.id.receiptBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        frameLayout = view.findViewById(R.id.frameLayoutReceiptOut);
        imageView = view.findViewById(R.id.receiptImageView);
        //Recycler View
        recyclerView = view.findViewById(R.id.recyclerViewReceipt);
        arrayList = new ArrayList<>();
        //Getting phone number to send receipt
        //Country Code picker
        codePicker = view.findViewById(R.id.countryCodeHolderReceipt);
        phoneNumber = view.findViewById(R.id.receiptPhoneNumberEditText);

        databaseReference = new FirebaseMethods().getDatabaseReference();

        initializeRecyclerView();
        initializeBusinessInfo();
        initializeSaleDetails();

        share = view.findViewById(R.id.shareReceiptButton);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Converting bitmap/layout to JPEG
                Bitmap bitmap = Bitmap.createBitmap(frameLayout.getWidth(), frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                frameLayout.draw(canvas);

                Bitmap bmp = null;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(byteArray);
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Showing dialog for either WhatsApp or other media interfaces
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                CharSequence[] options = new CharSequence[]{
                        "Share to WhatsApp", "Share to other"
                };
                builder.setTitle("What do you want to do?");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            codePicker.registerCarrierNumberEditText(phoneNumber);
                            phone1 = codePicker.getFullNumberWithPlus();
                            Log.e(TAG, "onClick: Number: " + phone1);
                            if (!phone1.equals("")) {
                                try {
                                    Intent intentApp = new Intent(Intent.ACTION_VIEW);
                                    String URL = "https://api.whatsapp.com/send?phone=" + phone1;
                                    intentApp.setData(Uri.parse(URL));
                                    intentApp.putExtra(Intent.EXTRA_STREAM, Uri.parse(new FilePaths().RECEIPT_TEMP_IMAGE));
                                    startActivity(intentApp);
                                } catch (Exception e) {
                                    Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onClick: Opening WhatsApp failed: " + e.getMessage());
                                }
                            } else {
                                Toast.makeText(requireActivity(), "Please" +
                                        " input a number to send to", Toast.LENGTH_SHORT).show();
                            }
                        } else if (which == 1) {
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("image/jpeg");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(new FilePaths().RECEIPT_TEMP_IMAGE));
                            startActivity(Intent.createChooser(shareIntent, "Share Image"));
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void initializeBusinessInfo() {
        databaseReference
                .child(getString(R.string.fbBusinessInfo)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    BusinessObject business = snapshot.getValue(BusinessObject.class);
                    assert business != null;

                    businessName.setText(business.getName());
                    Log.e(TAG, "onDataChange: Business Name: " + business.getName());
                    businessAddress.setText(business.getAddress());
                    phone.setText(business.getPhoneNumber());
                    email.setText(business.getEmailAddress());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initializeSaleDetails() {

        databaseReference
                .child(getString(R.string.fbTumiInfo))
                .child("Finances")
                .child("Sales")
                .child("Sale Details")
                .child(saleSelectedReceipt).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    SaleDetailObject saleDetailObject = snapshot.getValue(SaleDetailObject.class);
                    assert saleDetailObject != null;

                    date.setText(saleDetailObject.getDate());
                    total.setText(String.format("%s%s", getString(R.string.currencyIdentifier), saleDetailObject.getTotal()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void initializeRecyclerView() {
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        databaseReference
                .child("Tumi Information")
                .child("Finances")
                .child("Sales")
                .child("Finished Sales")
                .child(saleSelectedReceipt).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String productTitle = "",
                        productQuantity = "",
                        productPrice = "",
                        productTotal = "",
                        productKey = "";

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("productTitle").getValue() != null)
                        productTitle = dataSnapshot.child("productTitle").getValue().toString();
                    if (dataSnapshot.child("productQuantity").getValue() != null)
                        productQuantity = dataSnapshot.child("productQuantity").getValue().toString();
                    if (dataSnapshot.child("productPrice").getValue() != null)
                        productPrice = dataSnapshot.child("productPrice").getValue().toString();
                    if (dataSnapshot.child("productTotal").getValue() != null)
                        productTotal = dataSnapshot.child("productTotal").getValue().toString();
                    if (dataSnapshot.child("productKey").getValue() != null)
                        productKey = dataSnapshot.child("productKey").getValue().toString();

                    DisplayProductsObject displayProductsObject = new DisplayProductsObject(productTitle, productQuantity, productPrice, productTotal, productKey);
                    arrayList.add(displayProductsObject);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //Adapter
        adapter = new ReceiptAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }
}
