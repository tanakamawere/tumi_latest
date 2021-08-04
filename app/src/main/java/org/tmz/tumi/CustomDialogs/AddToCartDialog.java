package org.tmz.tumi.CustomDialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tmz.tumi.Finances.SaleCartActivity;
import org.tmz.tumi.R;

import java.util.HashMap;
import java.util.Objects;

public class AddToCartDialog extends DialogFragment {

    private TextView productTitle, productPrice;
    private String productKey, productTitleString, productPriceString, productQuantityString, productTotalString;
    private int setNewNumberInteger = 1;
    private String setNewNumberString = "";
    private EditText numberET;
    private Button subBtn, addBtn;
    private View view;
    private AlertDialog.Builder builder;

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_product_details, null);

        subBtn = view.findViewById(R.id.recordSaleSubBtn);
        addBtn = view.findViewById(R.id.recordSaleAddBtn);
        numberET = view.findViewById(R.id.recordSaleEditText);
        setNewNumberString = Integer.toString(setNewNumberInteger);
        productQuantityString = numberET.getText().toString();
        numberET.setText(setNewNumberString);
        numberET.setText("1");
        numberPicker();

        Bundle bundle = getArguments();
        assert bundle != null;

        productTitle = view.findViewById(R.id.bottomSheetProductTitle);
        productPrice = view.findViewById(R.id.bottomSheetProductPrice);

        //Taking information from the bundle into strings
        productTitleString = bundle.getString("productTitleRecordSale");
        productPriceString = bundle.getString("productSellingPriceRecordSale");
        productKey = bundle.getString("productKeyRecordSale");
        //Value from number picker

        //Display values
        productTitle.setText(productTitleString);
        productPrice.setText("USD $" + productPriceString + " each");

        builder.setView(view).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Add to cart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addProductToSaleCart();
                        dismissAllowingStateLoss();
                        Toast.makeText(getContext(), productTitleString + " added to cart successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), SaleCartActivity.class));
                    }
                });

        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void numberPicker() {

        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setNewNumberInteger > 1) {
                    setNewNumberInteger--;
                    setNewNumberString = Integer.toString(setNewNumberInteger);
                    numberET.setText(setNewNumberString);
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewNumberInteger++;
                setNewNumberString = Integer.toString(setNewNumberInteger);
                numberET.setText(setNewNumberString);
            }
        });
    }

    private void addProductToSaleCart() {
        productQuantityString = numberET.getText().toString();
        productTotalString = Double.toString(Double.parseDouble(productQuantityString) * Double.parseDouble(productPriceString));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Tumi Information")
                .child("Finances")
                .child("Sales")
                .child("Sales Cart")
                .child(productTitleString);

        HashMap<String, Object> map = new HashMap<>();
        map.put("productTitle", productTitleString);
        map.put("productQuantity", productQuantityString);
        map.put("productPrice", productPriceString);
        map.put("productTotal", productTotalString);
        map.put("productKey", productKey);

        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }
}
