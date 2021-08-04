package org.tmz.tumi.CustomDialogs;

import android.os.Bundle;
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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.DisplaySoldProductsAdapter;
import org.tmz.tumi.Finances.main.FragmentCreateReceipt;
import org.tmz.tumi.Objects.DisplayProductsObject;
import org.tmz.tumi.Objects.SaleDetailObject;
import org.tmz.tumi.R;

import java.util.ArrayList;
import java.util.Objects;

public class DisplaySoldProducts extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<DisplayProductsObject> arrayList;
    private String saleSelected;
    private DatabaseReference databaseReference;

    //Widgets
    private ImageButton close;
    private TextView date, name, phone, number, total;
    private Button options, receipt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_display_sold_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        assert bundle != null;
        saleSelected = bundle.getString("saleSelected");

        //Initialising Widgets
        close = view.findViewById(R.id.cancelDisplayProductButton);
        date = view.findViewById(R.id.dateFinishedSaleTextView);
        name = view.findViewById(R.id.nameFinishedSaleTextView);
        phone = view.findViewById(R.id.phoneFinishedSaleTextView);
        number = view.findViewById(R.id.numberFinishedSaleTextView);
        total = view.findViewById(R.id.totalFinishedSaleTextView);
        options = view.findViewById(R.id.optionsFinishedSaleButton);
        receipt = view.findViewById(R.id.sendReceiptButton);

        arrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerViewDisplaySoldProducts);
        initializeRecyclerView();
        initializeSaleDetails();

        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                Fragment fragment = new FragmentCreateReceipt();
                bundle1.putString(getString(R.string.selectedSaleReceipt), saleSelected);
                fragment.setArguments(bundle1);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutFinances, fragment)
                        .addToBackStack(null)
                        .commit();

                dismiss();
            }
        });
    }

    private void initializeSaleDetails() {

        //Getting Information from FireBase
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Tumi Information")
                .child("Finances")
                .child("Sales")
                .child("Sale Details")
                .child(saleSelected);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    SaleDetailObject saleDetailObject = snapshot.getValue(SaleDetailObject.class);
                    assert saleDetailObject != null;

                    if (saleDetailObject.getCustomerName().equals("null")) {
                        name.setVisibility(View.GONE);
                        phone.setVisibility(View.GONE);
                    } else {
                        name.setText(saleDetailObject.getCustomerName());
                        phone.setText(saleDetailObject.getCustomerPhone());
                    }
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

        //Getting Information from FireBase
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Tumi Information")
                .child("Finances")
                .child("Sales")
                .child("Finished Sales")
                .child(saleSelected);

        databaseReference.addValueEventListener(new ValueEventListener() {
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
        adapter = new DisplaySoldProductsAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }
}
