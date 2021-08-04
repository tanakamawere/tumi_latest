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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tmz.tumi.R;

import java.util.HashMap;
import java.util.Objects;

public class FragmentEditSaleCartDetails extends BottomSheetDialogFragment {

    private TextView productTitle, productPrice;
    private String productKeySalesCart,
            productTitleStringSalesCart,
            productPriceStringSalesCart,
            productQuantityStringSalesCart,
            productTotalStringSalesCart;
    private int setNewNumberInteger = 1;
    private String setNewNumberString = "";
    private EditText numberET;
    private Button subBtn, addBtn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_sale_cart_details, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        assert bundle != null;

        productTitle = view.findViewById(R.id.saleCartEditTitleTextView);
        productPrice = view.findViewById(R.id.saleCartEditPriceTextView);

        Button addToCartButton = view.findViewById(R.id.saleCartEditButton);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProductSaleCart();
            }
        });


        subBtn = view.findViewById(R.id.editQuantityEditCartSubBtn);
        addBtn = view.findViewById(R.id.editQuantityEditCartAddBtn);
        numberET = view.findViewById(R.id.editQuantityEditCart);
        setNewNumberString = Integer.toString(setNewNumberInteger);
        numberET.setText(setNewNumberString);
        numberET.setText(Objects.requireNonNull(bundle.getString("productQuantitySalesCart")));
        numberPicker();

        //Taking information from the bundle into strings
        productTitleStringSalesCart = bundle.getString("productTitleSalesCart");
        productPriceStringSalesCart = bundle.getString("productPriceSalesCart");
        productKeySalesCart = bundle.getString("productKeySalesCart");

        //Display values
        productTitle.setText(productTitleStringSalesCart);
        productPrice.setText("USD $" + productPriceStringSalesCart + " each");
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

    private void editProductSaleCart() {
        //Value from number picker
        productQuantityStringSalesCart = numberET.getText().toString();
        productTotalStringSalesCart = Double.toString(Double.parseDouble(productQuantityStringSalesCart) * Double.parseDouble(productPriceStringSalesCart));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Tumi Information")
                .child("Finances")
                .child("Sales")
                .child("Sales Cart")
                .child(productTitleStringSalesCart);

        HashMap<String, Object> map = new HashMap<>();
        map.put("productTitle", productTitleStringSalesCart);
        map.put("productQuantity", productQuantityStringSalesCart);
        map.put("productPrice", productPriceStringSalesCart);
        map.put("productTotal", productTotalStringSalesCart);
        map.put("productKey", productKeySalesCart);

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
