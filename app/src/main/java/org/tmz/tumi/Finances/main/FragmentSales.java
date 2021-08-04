package org.tmz.tumi.Finances.main;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.FinancesSalesAdapter;
import org.tmz.tumi.Finances.RecordSaleActivity;
import org.tmz.tumi.Objects.FinanceSaleObject;
import org.tmz.tumi.R;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentSales extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<FinanceSaleObject> arrayList;
    private TextView nullSalesTV, nullInternet;
    private ProgressBar progressBar;
    private ExtendedFloatingActionButton recordSaleFab;

    public static FragmentSales newInstance(int index) {
        FragmentSales fragment = new FragmentSales();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nullSalesTV = view.findViewById(R.id.nullStockSales);
        nullSalesTV.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progressBarSales);
        nullInternet = view.findViewById(R.id.nullInternetSalesTV);
        nullInternet.setVisibility(View.GONE);

        checkForInternetConnection();


        recordSaleFab = view.findViewById(R.id.fabRecordSaleFinances);
        recordSaleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), RecordSaleActivity.class));
            }
        });

        arrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerViewFinancesSales);
        initializeRecyclerView();
    }

    private void checkForInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            nullInternet.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void initializeRecyclerView() {
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        //Getting Information from FireBase
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Tumi Information")
                .child("Finances")
                .child("Sales")
                .child("Sale Details");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String total = "",
                            date = "",
                            saleID = "";

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("total").getValue() != null)
                            total = dataSnapshot.child("total").getValue().toString();
                        if (dataSnapshot.child("date").getValue() != null)
                            date = dataSnapshot.child("date").getValue().toString();
                        if (dataSnapshot.child("saleID").getValue() != null)
                            saleID = dataSnapshot.child("saleID").getValue().toString();

                        FinanceSaleObject financeSaleObject = new FinanceSaleObject(date, total, saleID);
                        arrayList.add(financeSaleObject);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                } else {
                    nullSalesTV.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Adapter
        recyclerViewAdapter = new FinancesSalesAdapter(arrayList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}