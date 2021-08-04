package org.tmz.tumi.Adapters;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import org.tmz.tumi.Objects.StockMainObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Stock.FragmentViewStockDetails;
import org.tmz.tumi.Stock.StockActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockListViewHolder> {

    ArrayList<StockMainObject> stockMainObjectArrayList;

    public StockAdapter(ArrayList<StockMainObject> stockMainObjectArrayList) {
        this.stockMainObjectArrayList = stockMainObjectArrayList;
    }

    @NonNull
    @Override
    public StockAdapter.StockListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_stock, parent, false);

        return new StockListViewHolder(layoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StockAdapter.StockListViewHolder viewHolder, final int position) {
        final StockMainObject stockMainObject = stockMainObjectArrayList.get(position);
        DecimalFormat decimalFormat = new DecimalFormat("####0.00");

        //This is where we get data from fireBase
        viewHolder.productTitleTextView.setText(stockMainObject.getProductTitle());
        viewHolder.productPriceTextView.setText("Selling Price: USD $" + stockMainObject.getProductSellingPrice());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Bundle bundle = new Bundle();
                bundle.putString("stockSelected", stockMainObject.getProductKey());

                FragmentViewStockDetails fragmentStockDetails = new FragmentViewStockDetails();

                fragmentStockDetails.setArguments(bundle);
                FragmentManager fragmentManager = ((StockActivity) v.getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out)
                        .replace(R.id.frameLayoutStock, fragmentStockDetails)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return stockMainObjectArrayList.size();
    }

    public class StockListViewHolder extends RecyclerView.ViewHolder {

        public TextView productTitleTextView, productQuantityTextView, productPriceTextView, productTotalTextView;
        public CardView cardView;

        public StockListViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.viewHolderStock);

            productTitleTextView = itemView.findViewById(R.id.stockProductTitleTextView);
            productQuantityTextView = itemView.findViewById(R.id.stockProductQuantityTextView);
            productPriceTextView = itemView.findViewById(R.id.stockProductPriceTextView);
            productTotalTextView = itemView.findViewById(R.id.stockProductTotalTextView);
        }
    }
}


