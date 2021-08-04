package org.tmz.tumi.Stock;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.CustomDialogs.AddToCartDialog;
import org.tmz.tumi.Main.Explore.FragmentCreateAd;
import org.tmz.tumi.Objects.StockMainObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.text.DecimalFormat;
import java.util.Objects;

public class FragmentViewStockDetails extends Fragment {

    private static final String TAG = "FragStockDetails";

    private String stockSelected;
    private DatabaseReference databaseReference;

    //Widgets
    private ImageButton close;
    private TextView buyingPrice, quantity, total, title, sellingPrice, grossProfit;
    private Button edit, advertise, sellProduct;
    private ImageButton delete;
    private ProgressDialog progressDialog;

    //Variables
    private long number;
    private DecimalFormat decimalFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_stock_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = new FirebaseMethods().getDatabaseReference()
                .child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbStock));

        final Bundle bundle = getArguments();
        assert bundle != null;
        stockSelected = bundle.getString("stockSelected");

        //Initialising Widgets
        buyingPrice = view.findViewById(R.id.buyingPriceStockDisplayTextView);
        quantity = view.findViewById(R.id.quantityStockDisplayTextView);
        total = view.findViewById(R.id.totalStockDisplayTextView);
        title = view.findViewById(R.id.titleStockDisplayTextView);
        sellingPrice = view.findViewById(R.id.sellingPriceStockDisplayTextView);
        grossProfit = view.findViewById(R.id.totalGrossProfitDisplayTextView);
        //ProgressDialog
        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setMessage(getString(R.string.pdUploading));
        progressDialog.setTitle(getString(R.string.pdPleaseWait));
        progressDialog.setCanceledOnTouchOutside(false);

        //Button Widgets
        delete = view.findViewById(R.id.deleteProductButton);
        edit = view.findViewById(R.id.editProductButton);
        advertise = view.findViewById(R.id.advertiseProductButton);
        sellProduct = view.findViewById(R.id.sellProductButton);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                progressDialog.show();
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            if (Objects.requireNonNull(childSnapshot.child("productKey").getValue()).toString().equals(stockSelected))
                                childSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(v.getContext(), "Product removed successfully.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(v.getContext(), StockActivity.class));
                                        } else {
                                            Toast.makeText(v.getContext(), "Failed to remove products", Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            if (Objects.requireNonNull(childSnapshot.child("productKey").getValue()).toString().equals(stockSelected)) {
                                Bundle bundle = new Bundle();
                                bundle.putString(getString(R.string.fromWhere), getString(R.string.fragmentNewProduct));
                                bundle.putString(getString(R.string.fragmentSelectedStock), Objects.requireNonNull(childSnapshot.child("productKey").getValue()).toString());

                                Fragment fragment2 = new FragmentUploadStockDetails();
                                fragment2.setArguments(bundle);

                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.frameLayoutStock, fragment2)
                                        .addToBackStack(null).commit();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        sellProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToCart();
            }
        });

        advertise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Fragment fragment = new FragmentCreateAd();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                Bundle bundle1 = new Bundle();
                bundle1.putString(getString(R.string.selectedStockAd), stockSelected);
                bundle1.putString(getString(R.string.fromWhere), getString(R.string.stockAdvertise));

                fragment.setArguments(bundle1);
                fragmentManager.beginTransaction().replace(R.id.frameLayoutStock, fragment)
                        .addToBackStack(null).commit();
            }
        });

        decimalFormat = new DecimalFormat("####0.00");

        initializeTextView();
        //countNumberOfAds();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void countNumberOfAds() {

        //todo
        FirebaseMethods firebaseMethods = new FirebaseMethods();
        DatabaseReference mDatabase = firebaseMethods.getDatabaseReference();
        mDatabase.child("Explore").child("Advertisements").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                number = snapshot.getChildrenCount();
                Log.e(TAG, "onDataChange: Number inside method: " + number);
                if (number == 3) {
                    advertise.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(requireActivity(), "You have maximum" +
                                    " number of ads (3) uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addProductToCart() {
        databaseReference.child(stockSelected).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final StockMainObject stockMainObject = snapshot.getValue(StockMainObject.class);
                    assert stockMainObject != null;

                    FragmentManager fragmentManager = ((StockActivity) requireContext()).getSupportFragmentManager();
                    Bundle bundleRecordSale = new Bundle();
                    bundleRecordSale.putString("productTitleRecordSale", stockMainObject.getProductTitle());
                    bundleRecordSale.putString("productQuantityRecordSale", stockMainObject.getProductQuantity());
                    bundleRecordSale.putString("productSellingPriceRecordSale", stockMainObject.getProductSellingPrice());
                    bundleRecordSale.putString("productKeyRecordSale", stockMainObject.getProductKey());

                    AddToCartDialog productDetailsDialog = new AddToCartDialog();
                    productDetailsDialog.setArguments(bundleRecordSale);
                    productDetailsDialog.show(fragmentManager, "productDialog");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeTextView() {
        databaseReference.child(stockSelected).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    StockMainObject stockMainObject = snapshot.getValue(StockMainObject.class);
                    assert stockMainObject != null;

                    buyingPrice.setText(getString(R.string.currencyIdentifier) + stockMainObject.getProductBuyingPrice());
                    quantity.setText(stockMainObject.getProductQuantity() + " units");
                    title.setText(stockMainObject.getProductTitle());
                    sellingPrice.setText(getString(R.string.currencyIdentifier) + stockMainObject.getProductSellingPrice() + " each");

                    //For product value
                    double productQuantityDouble = Double.parseDouble(stockMainObject.getProductQuantity());
                    double productBuyingTotalDouble = Double.parseDouble(stockMainObject.getProductBuyingPrice());
                    final double productValueDouble = productQuantityDouble * productBuyingTotalDouble;

                    //AccountingCalculations for gross profit done here
                    double productSellingTotalDouble = Double.parseDouble(stockMainObject.getProductSellingPrice());
                    final double productGrossProfitDouble = productQuantityDouble * (productSellingTotalDouble - productBuyingTotalDouble);

                    total.setText(getString(R.string.currencyIdentifier) + decimalFormat.format(productValueDouble));
                    grossProfit.setText(getString(R.string.currencyIdentifier) + decimalFormat.format(productGrossProfitDouble));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
