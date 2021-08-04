package org.tmz.tumi.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tmz.tumi.Finances.FinancesActivity;
import org.tmz.tumi.Objects.DisplayProductsObject;
import org.tmz.tumi.R;

import java.util.ArrayList;
import java.util.Objects;

public class DisplaySoldProductsAdapter extends RecyclerView.Adapter<DisplaySoldProductsAdapter.DisplaySoldProductsViewHolder> {

    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
            .child("Tumi Information")
            .child("Finances")
            .child("Sales")
            .child("Finished Sales");
    ArrayList<DisplayProductsObject> productsObjects;

    public DisplaySoldProductsAdapter(ArrayList<DisplayProductsObject> productsObjects) {
        this.productsObjects = productsObjects;
    }

    @NonNull
    @Override
    public DisplaySoldProductsAdapter.DisplaySoldProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_display_sale, parent, false);

        return new DisplaySoldProductsViewHolder(layoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DisplaySoldProductsAdapter.DisplaySoldProductsViewHolder viewHolder, final int position) {
        final DisplayProductsObject displaySoldProducts = productsObjects.get(position);

        //This is where we get data from fireBase
        viewHolder.productTitleTextView.setText("Title:                 " + displaySoldProducts.getProductTitle());
        viewHolder.productQuantityTextView.setText("Quantity:           " + displaySoldProducts.getProductQuantity() + " units.");
        viewHolder.productPriceTextView.setText("Price:                 USD $" + displaySoldProducts.getProductPrice());
        viewHolder.productTotalTextView.setText("Amount:                 USD $" + displaySoldProducts.getProductTotal());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Are you sure you want to delete this record?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                        progressDialog.setMessage("Please wait while we are updating your info...");
                        progressDialog.show();

                        String selectedExpenseID = productsObjects.get(position).getProductKey();

                        databaseReference.child(selectedExpenseID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(v.getContext(), "Success!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(v.getContext(), FinancesActivity.class);
                                    v.getContext().startActivity(intent);
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsObjects.size();
    }

    public class DisplaySoldProductsViewHolder extends RecyclerView.ViewHolder {

        public TextView productTitleTextView, productQuantityTextView, productPriceTextView, productTotalTextView;
        public CardView cardView;

        public DisplaySoldProductsViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.viewHolderStock);

            productTitleTextView = itemView.findViewById(R.id.stockProductTitleTextView);
            productQuantityTextView = itemView.findViewById(R.id.stockProductQuantityTextView);
            productPriceTextView = itemView.findViewById(R.id.stockProductPriceTextView);
            productTotalTextView = itemView.findViewById(R.id.stockProductTotalTextView);
        }
    }
}


