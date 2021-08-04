package org.tmz.tumi.Finances;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.RecordSaleAdapter;
import org.tmz.tumi.Objects.RecordSaleObject;
import org.tmz.tumi.R;

import java.util.ArrayList;
import java.util.Objects;

public class RecordSaleActivity extends AppCompatActivity {

    private DatabaseReference databaseReference, goalDB;
    private ArrayList<RecordSaleObject> arrayList;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private TextView stockTV1, internetTV;
    private ProgressBar progressBar;
    private Button checkOutBtn;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_sale);

        //the following is to put a back button in the actionBar that is the toolbar

        stockTV1 = findViewById(R.id.nullStockRecordSaleTV1);
        stockTV1.setVisibility(View.GONE);
        internetTV = findViewById(R.id.nullInternetRecordSalesTV);
        internetTV.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBarRecordSale);

        arrayList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Tumi Information")
                .child("Stock");

        checkOutBtn = findViewById(R.id.checkOutButton);
        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordSaleActivity.this, SaleCartActivity.class));
            }
        });

        initialiseRecyclerView();
        checkForInternetConnection();
    }

    private void checkForInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) RecordSaleActivity.this.getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            internetTV.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            checkOutBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void initialiseRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewRecordSale);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String title = "", quantity = "";

                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        if (childSnapshot.child("productTitle").getValue() != null)
                            title = Objects.requireNonNull(childSnapshot.child("productTitle").getValue()).toString();
                        if (childSnapshot.child("productQuantity").getValue() != null)
                            quantity = Objects.requireNonNull(childSnapshot.child("productQuantity").getValue()).toString();

                        RecordSaleObject recordSaleObject = new RecordSaleObject(title, quantity);
                        arrayList.add(recordSaleObject);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                } else {
                    stockTV1.setVisibility(View.VISIBLE);
                    checkOutBtn.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerViewAdapter = new RecordSaleAdapter(arrayList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}
