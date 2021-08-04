package org.tmz.tumi.Adapters;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Finances.FragmentEditSaleCartDetails;
import org.tmz.tumi.Finances.SaleCartActivity;
import org.tmz.tumi.Objects.SalesCartObject;
import org.tmz.tumi.R;

import java.util.ArrayList;
import java.util.Objects;

public class SalesCartAdapter extends RecyclerView.Adapter<SalesCartAdapter.SalesCartViewHolder> {

    ArrayList<SalesCartObject> salesCartObjectArrayList;
    FragmentManager fragmentManager;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
            .child("Tumi Information")
            .child("Finances")
            .child("Sales")
            .child("Sales Cart");

    public SalesCartAdapter(ArrayList<SalesCartObject> salesCartObjectArrayList) {
        this.salesCartObjectArrayList = salesCartObjectArrayList;
    }

    @NonNull
    @Override
    public SalesCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_sale_cart, parent, false);
        return new SalesCartViewHolder(myView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SalesCartViewHolder holder, final int position) {
        final SalesCartObject salesCartObject = salesCartObjectArrayList.get(position);

        holder.titleTV.setText(salesCartObject.getProductTitle());
        holder.quantityTV.setText(salesCartObject.getProductQuantity() + " unit(s)");
        holder.totalTV.setText("Total amount: USD $" + salesCartObject.getProductTotal());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                CharSequence[] options = new CharSequence[]{
                        "Edit product details", "Delete product"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("What do you want to do?");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                        String poshto = salesCartObjectArrayList.get(position).getProductKey();
                                        FragmentManager fragmentManager = ((SaleCartActivity) v.getContext()).getSupportFragmentManager();

                                        if (Objects.requireNonNull(childSnapshot.child("productKey").getValue()).toString().equals(poshto)) {
                                            Bundle bundle3 = new Bundle();
                                            bundle3.putString("productTitleSalesCart", Objects.requireNonNull(childSnapshot.child("productTitle").getValue()).toString());
                                            bundle3.putString("productQuantitySalesCart", Objects.requireNonNull(childSnapshot.child("productQuantity").getValue()).toString());
                                            bundle3.putString("productPriceSalesCart", Objects.requireNonNull(childSnapshot.child("productPrice").getValue()).toString());
                                            bundle3.putString("productKeySalesCart", Objects.requireNonNull(childSnapshot.child("productKey").getValue()).toString());

                                            Fragment fragment = new FragmentEditSaleCartDetails();
                                            fragment.setArguments(bundle3);
                                            fragmentManager.beginTransaction()
                                                    .setCustomAnimations(
                                                            R.anim.slide_in,  // enter
                                                            R.anim.fade_out,  // exit
                                                            R.anim.fade_in,   // popEnter
                                                            R.anim.slide_out  // popExit
                                                    ).replace(R.id.frameLayoutSaleCart, fragment)
                                                    .addToBackStack(null).commit();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else if (which == 1) {
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                        String poshto = salesCartObjectArrayList.get(position).getProductKey();

                                        if (Objects.requireNonNull(childSnapshot.child("productKey").getValue()).toString().equals(poshto))
                                            childSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(v.getContext(), "Product removed successfully.", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(v.getContext(), SaleCartActivity.class);
                                                        v.getContext().startActivity(intent);
                                                    }
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
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return salesCartObjectArrayList.size();
    }

    public class SalesCartViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTV, quantityTV, totalTV;

        public SalesCartViewHolder(View view) {
            super(view);

            titleTV = view.findViewById(R.id.saleCartTitleTextView);
            quantityTV = view.findViewById(R.id.saleCartQuantityTextView);
            totalTV = view.findViewById(R.id.saleCartTotalTextView);
        }
    }
}
