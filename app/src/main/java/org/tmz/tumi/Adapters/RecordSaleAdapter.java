package org.tmz.tumi.Adapters;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.CustomDialogs.AddToCartDialog;
import org.tmz.tumi.Finances.RecordSaleActivity;
import org.tmz.tumi.Objects.RecordSaleObject;
import org.tmz.tumi.R;

import java.util.ArrayList;
import java.util.Objects;

public class RecordSaleAdapter extends RecyclerView.Adapter<RecordSaleAdapter.RecordSaleViewHolder> {

    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
            .child("Tumi Information")
            .child("Stock");
    ArrayList<RecordSaleObject> recordSaleObjectsArrayList;

    public RecordSaleAdapter(ArrayList<RecordSaleObject> recordSaleObjectsArrayList) {
        this.recordSaleObjectsArrayList = recordSaleObjectsArrayList;
    }

    public static void showDialogAllowingStateLoss(FragmentManager fragmentManager, DialogFragment dialogFragment, String tag) {

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(dialogFragment, tag);
        ft.commitAllowingStateLoss();
    }

    @NonNull
    @Override
    public RecordSaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_sale_products, parent, false);
        return new RecordSaleViewHolder(myView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecordSaleViewHolder holder, final int position) {
        final RecordSaleObject recordSaleObject = recordSaleObjectsArrayList.get(position);

        holder.productTitle.setText(recordSaleObject.getProductTitle());
        holder.productQuantity.setText(recordSaleObject.getProductQuantity() + " units in stock");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            FragmentManager fragmentManager = ((RecordSaleActivity) v.getContext()).getSupportFragmentManager();
                            String poshto = recordSaleObjectsArrayList.get(position).getProductTitle();

                            if (childSnapshot.child("productTitle").getValue().toString().equals(poshto)) {
                                Bundle bundleRecordSale = new Bundle();
                                bundleRecordSale.putString("productTitleRecordSale", Objects.requireNonNull(childSnapshot.child("productTitle").getValue()).toString());
                                bundleRecordSale.putString("productQuantityRecordSale", Objects.requireNonNull(childSnapshot.child("productQuantity").getValue()).toString());
                                bundleRecordSale.putString("productSellingPriceRecordSale", Objects.requireNonNull(childSnapshot.child("productSellingPrice").getValue()).toString());
                                bundleRecordSale.putString("productKeyRecordSale", Objects.requireNonNull(childSnapshot.child("productKey").getValue()).toString());

                                AddToCartDialog productDetailsDialog = new AddToCartDialog();
                                productDetailsDialog.setArguments(bundleRecordSale);

                                showDialogAllowingStateLoss(fragmentManager, productDetailsDialog, null);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordSaleObjectsArrayList.size();
    }

    public class RecordSaleViewHolder extends RecyclerView.ViewHolder {

        public TextView productTitle, productQuantity;

        public RecordSaleViewHolder(View view) {
            super(view);

            productQuantity = view.findViewById(R.id.recordSaleQuantityTextView);
            productTitle = view.findViewById(R.id.recordSaleTitleTextView);
        }
    }
}
