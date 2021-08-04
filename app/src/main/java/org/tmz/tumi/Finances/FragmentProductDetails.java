package org.tmz.tumi.Finances;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tmz.tumi.R;

import java.util.HashMap;
import java.util.Objects;

public class FragmentProductDetails extends Fragment {

    private TextView productTitle, productPrice;
    private String productKey, productTitleString, productPriceString, productQuantityString, productTotalString;
    private int setNewNumberInteger = 1;
    private String setNewNumberString = "";
    private EditText numberET;
    private Button subBtn, addBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_details, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        assert bundle != null;

        productTitle = view.findViewById(R.id.bottomSheetProductTitle);
        productPrice = view.findViewById(R.id.bottomSheetProductPrice);

        subBtn = view.findViewById(R.id.recordSaleSubBtn);
        addBtn = view.findViewById(R.id.recordSaleAddBtn);
        numberET = view.findViewById(R.id.recordSaleEditText);
        setNewNumberString = Integer.toString(setNewNumberInteger);
        productQuantityString = numberET.getText().toString();
        numberET.setText(setNewNumberString);
        numberET.setText("1");
        numberPicker();

        //Taking information from the bundle into strings
        productTitleString = bundle.getString("productTitleRecordSale");
        productPriceString = bundle.getString("productPriceRecordSale");
        productKey = bundle.getString("productKeyRecordSale");
        //Value from number picker

        //Display values
        productTitle.setText(productTitleString);
        productPrice.setText("USD $" + productPriceString + " each");
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
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Product added to cart successfully.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), SaleCartActivity.class));
                }
            }
        });
    }
}
