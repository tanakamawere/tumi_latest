package org.tmz.tumi.Stock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.StockAdapter;
import org.tmz.tumi.Main.Dashboard.DashboardActivity;
import org.tmz.tumi.Objects.StockMainObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.Miscellaneous;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class StockActivity extends AppCompatActivity {

    private static final String TAG = "StockActivity";

    public ExtendedFloatingActionButton addNewStock;
    //For database queries
    String productTitle = "";
    String productQuantity = "",
            productValue = "",
            productKey = "",
            productSellingPrice = "",
            productBuyingPrice = "",
            productGrossProfit = "";
    ArrayList<StockMainObject> stockList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ProgressBar progressBar;
    private TextView stockTV1, internetTV, costOfGoods, expectedGrossProfit;
    private DatabaseReference databaseReference;
    private FirebaseMethods firebaseMethods;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        firebaseMethods = new FirebaseMethods();
        databaseReference = firebaseMethods.getDatabaseReference();

        stockTV1 = findViewById(R.id.nullStockTV1);
        internetTV = findViewById(R.id.nullInternetStockTV);
        stockTV1.setVisibility(View.GONE);
        internetTV.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBarStock);
        addNewStock = findViewById(R.id.fabShowOptions);
        stockList = new ArrayList<>();

        addNewStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment2 = new FragmentUploadStockDetails();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.fromWhere), getString(R.string.fragmentNewProduct));
                bundle.putString(getString(R.string.fragmentSelectedStock), getString(R.string.bundleNull));

                fragment2.setArguments(bundle);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frameLayoutStock, fragment2)
                        .addToBackStack(null).commit();
            }
        });

        decimalFormat = new DecimalFormat("####0.00");

        checkForInternetConnection();
        populateRecyclerViewStock();
    }

    private void checkForInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) StockActivity.this.getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            internetTV.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            addNewStock.setVisibility(View.GONE);
        }
    }

    private void populateRecyclerViewStock() {
        //RecyclerView Instances
        recyclerView = findViewById(R.id.recylerViewStockMain);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        //LayoutManager
        recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        //Getting Information from FireBase
        final DatabaseReference databaseReference = new FirebaseMethods().getDatabaseReference()
                .child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbStock));

        final DatabaseReference nDB = new FirebaseMethods().getDatabaseReference()
                .child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbNotifications));

        final Miscellaneous misc = new Miscellaneous();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        productTitle = dataSnapshot.child("productTitle").getValue().toString();
                        productQuantity = dataSnapshot.child("productQuantity").getValue().toString();
                        productKey = dataSnapshot.child("productKey").getValue().toString();
                        productSellingPrice = dataSnapshot.child("productSellingPrice").getValue().toString();
                        productBuyingPrice = dataSnapshot.child("productBuyingPrice").getValue().toString();

                        StockMainObject stockMainObject = new StockMainObject(productTitle, productQuantity, productBuyingPrice, productSellingPrice, productValue, productKey, productGrossProfit);
                        stockList.add(stockMainObject);
                        recyclerViewAdapter.notifyDataSetChanged();

                        for (int i = 1; i < snapshot.getChildrenCount(); i++) {
                            //Checking if the stock object hasn't already been uploaded as a notification
                            if (Integer.parseInt(productQuantity) <= 5) {
                                nDB.child(productKey).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snap) {
                                        if (!snap.exists()) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("title", productTitle);
                                            hashMap.put("quantity", productQuantity);
                                            hashMap.put("key", productKey);
                                            hashMap.put("date", misc.getDate());
                                            hashMap.put("time", misc.getTime());
                                            hashMap.put("description", getString(R.string.lowStock));

                                            nDB.child(productKey).updateChildren(hashMap);
                                        } else {
                                            Log.e(TAG, "onDataChange: No stock is below five units");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                } else {
                    stockTV1.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Adapter
        recyclerViewAdapter = new StockAdapter(stockList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(StockActivity.this, DashboardActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivityForResult(new Intent(getApplicationContext(), StockActivity.class), 0);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    //Calculations
    private void displayStockTotal() {
        //Displaying Total Price of Stock
        databaseReference
                .child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbStock))
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            double stockTotal = 0;
                            for (DataSnapshot childSnapShot : snapshot.getChildren()) {
                                stockTotal = stockTotal + Double.parseDouble(Objects.requireNonNull(childSnapShot.child("productValue").getValue()).toString());
                                costOfGoods.setText(getString(R.string.currencyIdentifier) + decimalFormat.format(stockTotal));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void displayGrossProfit() {
        //Displaying Total Price of Stock
        databaseReference
                .child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbStock))
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            double grossTotal = 0;
                            for (DataSnapshot childSnapShot : snapshot.getChildren()) {
                                grossTotal = grossTotal + Double.parseDouble(Objects.requireNonNull(childSnapShot.child("productGrossProfit").getValue()).toString());
                                expectedGrossProfit.setText(getString(R.string.currencyIdentifier) + decimalFormat.format(grossTotal));
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}