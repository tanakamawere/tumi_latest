package org.tmz.tumi.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tmz.tumi.Objects.DisplayProductsObject;
import org.tmz.tumi.R;

import java.util.ArrayList;
import java.util.Objects;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder> {

    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
            .child("Tumi Information")
            .child("Finances")
            .child("Sales")
            .child("Finished Sales");
    ArrayList<DisplayProductsObject> productsObjects;

    public ReceiptAdapter(ArrayList<DisplayProductsObject> productsObjects) {
        this.productsObjects = productsObjects;
    }

    @NonNull
    @Override
    public ReceiptAdapter.ReceiptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_simple_receipt, parent, false);

        return new ReceiptViewHolder(layoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReceiptAdapter.ReceiptViewHolder viewHolder, final int position) {
        final DisplayProductsObject displaySoldProducts = productsObjects.get(position);

        //This is where we get data from fireBase
        viewHolder.productTitleTextView.setText(displaySoldProducts.getProductTitle());
        viewHolder.productQuantityTextView.setText(displaySoldProducts.getProductQuantity());
        viewHolder.productPriceTextView.setText(displaySoldProducts.getProductPrice());
        viewHolder.productTotalTextView.setText(displaySoldProducts.getProductTotal());
    }

    @Override
    public int getItemCount() {
        return productsObjects.size();
    }

    public class ReceiptViewHolder extends RecyclerView.ViewHolder {

        public TextView productTitleTextView, productQuantityTextView, productPriceTextView, productTotalTextView;
        public CardView cardView;

        public ReceiptViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.viewHolderStock);

            productTitleTextView = itemView.findViewById(R.id.titleReceiptVHTextView);
            productQuantityTextView = itemView.findViewById(R.id.quantityReceiptVHTextView);
            productPriceTextView = itemView.findViewById(R.id.priceReceiptVHTextView);
            productTotalTextView = itemView.findViewById(R.id.totalReceiptVHTextView);
        }
    }
}


