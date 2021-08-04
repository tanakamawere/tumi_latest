package org.tmz.tumi.Finances;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.SalesCartAdapter;
import org.tmz.tumi.Objects.SalesCartObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.Miscellaneous;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SaleCartActivity extends AppCompatActivity {
    private static final String TAG = "SaleCartActivity";
    public double stockTotal = 0, initialGP;
    //RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<SalesCartObject> salesCartObjectArrayList;
    private DatabaseReference databaseReference, goalDB;
    private DecimalFormat decimalFormat;
    private String title = "", quantity = "", total = "", key = "", saleID, displayTotal;
    private String mTitle, mQuantity, mTotal, mKey, sTitle, sQuantity, sTotal, sKey;
    private Intent intentInfo;

    //Widgets
    private ImageButton toggleLinearLayout1, toggleLinearLayout2;
    private TextInputEditText name, phone;
    private TextView totalOfSaleTV;
    private Button button;
    private LinearLayout linearLayout, linearLayoutCheckout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_cart);

        name = findViewById(R.id.customerNameCartEditText);
        phone = findViewById(R.id.customerPhoneNumberCartEditText);

        toggleLinearLayout1 = findViewById(R.id.dropDownSaleCartButton);
        linearLayout = findViewById(R.id.linearLayoutSaleCart);
        linearLayout.setVisibility(View.GONE);
        toggleLinearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayout.getVisibility() == View.GONE) {
                    linearLayout.setVisibility(View.VISIBLE);
                    toggleLinearLayout1.setBackground(ContextCompat.getDrawable(SaleCartActivity.this, R.drawable.ic_baseline_keyboard_arrow_up_24));
                } else if (linearLayout.getVisibility() == View.VISIBLE) {
                    linearLayout.setVisibility(View.GONE);
                    toggleLinearLayout1.setBackground(ContextCompat.getDrawable(SaleCartActivity.this, R.drawable.ic_baseline_keyboard_arrow_down_24));
                }
            }
        });

        //The checkOut details linear layout
        toggleLinearLayout2 = findViewById(R.id.dropDownCheckOutButton);
        linearLayoutCheckout = findViewById(R.id.linearLayoutCheckOut);
        linearLayoutCheckout.setVisibility(View.GONE);
        toggleLinearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayoutCheckout.getVisibility() == View.GONE) {
                    linearLayoutCheckout.setVisibility(View.VISIBLE);
                    toggleLinearLayout2.setBackground(ContextCompat.getDrawable(SaleCartActivity.this, R.drawable.ic_baseline_keyboard_arrow_up_24));
                } else if (linearLayoutCheckout.getVisibility() == View.VISIBLE) {
                    linearLayoutCheckout.setVisibility(View.GONE);
                    toggleLinearLayout2.setBackground(ContextCompat.getDrawable(SaleCartActivity.this, R.drawable.ic_baseline_keyboard_arrow_down_24));
                }
            }
        });

        recyclerView = findViewById(R.id.recyclerViewSaleCart);
        salesCartObjectArrayList = new ArrayList<>();
        totalOfSaleTV = findViewById(R.id.saleCartTotalPriceTextView);

        databaseReference = new FirebaseMethods().getDatabaseReference()
                .child(getString(R.string.fbTumiInfo));

        decimalFormat = new DecimalFormat("####0.00");

        button = findViewById(R.id.saleCartBtn);
        saleID = databaseReference
                .child("Finances")
                .child("Sales")
                .child("Finished Sales").push().getKey();

        assert saleID != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    assert saleID != null;
                    changeSaleLocation(databaseReference
                            .child("Finances")
                            .child("Sales")
                            .child("Sales Cart"), databaseReference
                            .child("Finances")
                            .child("Sales")
                            .child("Finished Sales").child(saleID));
                } catch (Exception e) {
                    Toast.makeText(SaleCartActivity.this, "There has been an error. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            initializeRecyclerView();
            displayTotalSale();
        } catch (Exception s) {
            Log.e(TAG, s.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SaleCartActivity.this, RecordSaleActivity.class));
        finish();
    }

    private void displayTotalSale() {
        //Displaying Total Price of Stock
        databaseReference.child(getString(R.string.fbFinances))
                .child("Sales")
                .child("Sales Cart").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapShot : snapshot.getChildren()) {
                        stockTotal = stockTotal + Double.parseDouble(Objects.requireNonNull(childSnapShot.child("productTotal").getValue()).toString());

                        displayTotal = Double.toString(stockTotal);
                        totalOfSaleTV.setText(getResources().getString(R.string.currencyTitle) + decimalFormat.format(stockTotal));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initializeRecyclerView() {
        recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        databaseReference
                .child("Finances")
                .child("Sales")
                .child("Sales Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        if (childSnapshot.child("productTitle").getValue() != null)
                            title = Objects.requireNonNull(childSnapshot.child("productTitle").getValue()).toString();
                        if (childSnapshot.child("productQuantity").getValue() != null)
                            quantity = Objects.requireNonNull(childSnapshot.child("productQuantity").getValue()).toString();
                        if (childSnapshot.child("productTotal").getValue() != null)
                            total = Objects.requireNonNull(childSnapshot.child("productTotal").getValue()).toString();
                        if (childSnapshot.child("productKey").getValue() != null)
                            key = Objects.requireNonNull(childSnapshot.child("productKey").getValue()).toString();

                        SalesCartObject salesCartObject = new SalesCartObject(title, quantity, total, key);
                        salesCartObjectArrayList.add(salesCartObject);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerViewAdapter = new SalesCartAdapter(salesCartObjectArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void changeSaleLocation(DatabaseReference fromPath, final DatabaseReference toPath) {

        final String customerName = name.getText().toString();
        final String customerPhone = phone.getText().toString();

        if (salesCartObjectArrayList.size() == 0) {
            button.setEnabled(false);
        } else {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                Miscellaneous miscellaneous = new Miscellaneous();

                                HashMap<String, Object> map = new HashMap<>();
                                map.put("date", miscellaneous.getDate());
                                map.put("total", displayTotal);
                                map.put("saleID", saleID);
                                if (customerName.equals("") && customerPhone.equals("")) {
                                    map.put("customerName", "null");
                                    map.put("customerPhone", "null");
                                } else if (!customerName.equals("") && !customerPhone.equals("")) {
                                    map.put("customerName", customerName);
                                    map.put("customerPhone", customerPhone);
                                }
                                databaseReference
                                        .child("Finances")
                                        .child("Sales")
                                        .child("Sale Details").child(saleID).updateChildren(map);
                                Toast.makeText(SaleCartActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SaleCartActivity.this, FinancesActivity.class));
                                finishAffinity();

                            } else {
                                Toast.makeText(SaleCartActivity.this, "Failure to update information", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            fromPath.addListenerForSingleValueEvent(valueEventListener);
        }

        oneAdjustStock();
    }

    private void oneAdjustStock() {

        databaseReference
                .child("Finances")
                .child("Sales")
                .child("Sales Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                for (DataSnapshot childSnap : snapshot.getChildren()) {
                    mTitle = Objects.requireNonNull(childSnap.child("productTitle").getValue()).toString();
                    mQuantity = Objects.requireNonNull(childSnap.child("productQuantity").getValue()).toString();


                    final DatabaseReference databaseReferenceStock = new FirebaseMethods().getDatabaseReference()
                            .child(getString(R.string.fbTumiInfo))
                            .child(getString(R.string.fbStock));
                    databaseReferenceStock.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            for (DataSnapshot childless : snapshot1.getChildren()) {
                                sTitle = Objects.requireNonNull(childless.child("productTitle").getValue()).toString();
                                sQuantity = Objects.requireNonNull(childless.child("productQuantity").getValue()).toString();
                                sKey = Objects.requireNonNull(childless.child("productKey").getValue()).toString();

                                Log.e(TAG, "onDataChange: Snapshot Children count: " + snapshot.getChildrenCount());

                                for (int i = 1; i <= snapshot.getChildrenCount(); i++) {
                                    if (sTitle.equals(mTitle)) {
                                        String newQuantity = Integer.toString(Integer.parseInt(sQuantity) - Integer.parseInt(mQuantity));
                                        mQuantity = Integer.toString(0);

                                        HashMap<String, Object> update1 = new HashMap<>();
                                        update1.put("productQuantity", newQuantity);
                                        databaseReferenceStock.child(sKey).updateChildren(update1);
                                        databaseReference.child("Finances").child("Sales").child("Sales Cart")
                                                .child(sTitle).removeValue();
                                        sTitle = "";
                                        sKey = "";
                                        mTitle = "";
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
