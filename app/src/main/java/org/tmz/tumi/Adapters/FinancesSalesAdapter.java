package org.tmz.tumi.Adapters;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tmz.tumi.CustomDialogs.DisplaySoldProducts;
import org.tmz.tumi.Finances.FinancesActivity;
import org.tmz.tumi.Objects.FinanceSaleObject;
import org.tmz.tumi.R;

import java.util.ArrayList;
import java.util.Objects;

public class FinancesSalesAdapter extends RecyclerView.Adapter<FinancesSalesAdapter.FinancesSalesViewHolder> {

    ArrayList<FinanceSaleObject> financeSaleObjectsArrayList;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
            .child("Tumi Information")
            .child("Finances")
            .child("Sales")
            .child("Finished Sales");
    String randomKey = "";

    public FinancesSalesAdapter(ArrayList<FinanceSaleObject> financeSaleObjectsArrayList) {
        this.financeSaleObjectsArrayList = financeSaleObjectsArrayList;
    }

    @NonNull
    @Override
    public FinancesSalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_finances_sales_fragment, parent, false);
        return new FinancesSalesViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final FinancesSalesViewHolder holder, final int position) {
        final FinanceSaleObject financeSaleObject = financeSaleObjectsArrayList.get(position);

        holder.date.setText("Date sold:          " + financeSaleObject.getDate());
        holder.total.setText("Amount:             USD$" + financeSaleObject.getTotal());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String poshto = financeSaleObjectsArrayList.get(position).getSaleID();

                Bundle bundleFSA = new Bundle();
                bundleFSA.putString("saleSelected", poshto);

                DisplaySoldProducts displaySoldProducts = new DisplaySoldProducts();
                displaySoldProducts.setArguments(bundleFSA);
                FragmentManager fragmentManager = ((FinancesActivity) v.getContext()).getSupportFragmentManager();
                displaySoldProducts.show(fragmentManager, "ModalBottomSheet");
            }
        });
    }

    @Override
    public int getItemCount() {
        return financeSaleObjectsArrayList.size();
    }

    public static class FinancesSalesViewHolder extends RecyclerView.ViewHolder {

        public TextView date, total;

        public FinancesSalesViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.financesSalesDateTextView);
            total = itemView.findViewById(R.id.financesSalesTotalSoldTextView);
        }
    }
}
