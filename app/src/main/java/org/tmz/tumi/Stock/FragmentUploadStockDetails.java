package org.tmz.tumi.Stock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Objects.StockMainObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.util.HashMap;
import java.util.Objects;

public class FragmentUploadStockDetails extends Fragment {
    private static final String TAG = "FragmentUploadStockDetails";

    private TextView fragmentTextView;
    private TextInputEditText titleET, buyingPriceET, sellingPriceET;
    private Button confirmButton, subBtn, addBtn;
    private EditText numberET;
    private DatabaseReference editDB, addDb;
    private String locationFrom, selectedStock, productName, productBuyingPrice, productSellingPrice, productQuantity, newProductKey;
    private int setNewNumberInteger = 1;
    private double productBuyingTotalDouble, productSellingTotalDouble;
    private String setNewNumberString = "";
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_stock_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set previous values from Bundle
        Bundle bundle = getArguments();
        assert bundle != null;
        locationFrom = bundle.getString(getString(R.string.fromWhere));
        selectedStock = bundle.getString(getString(R.string.fragmentSelectedStock));

        //DatabaseReferences
        editDB = new FirebaseMethods().getDatabaseReference()
                .child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbStock))
                .child(selectedStock);

        addDb = new FirebaseMethods().getDatabaseReference()
                .child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbStock));

        newProductKey = new FirebaseMethods().getDatabaseReference()
                .child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbStock)).push().getKey();

        //Getting stock details for editing
        if (locationFrom.equals(getString(R.string.fragmentEditProduct))) {
            getDataFromDatabase();
        }

        //Widgets
        progressDialog = new ProgressDialog(requireActivity());
        titleET = view.findViewById(R.id.addNewProductEditText);
        buyingPriceET = view.findViewById(R.id.addNewProductBuyingPriceEditText);
        sellingPriceET = view.findViewById(R.id.addNewProductSellingPriceEditText);
        subBtn = view.findViewById(R.id.editStockSubBtn);
        addBtn = view.findViewById(R.id.editStockAddBtn);
        numberET = view.findViewById(R.id.editStockEditText);
        confirmButton = view.findViewById(R.id.addNewProductBtn);
        numberET = view.findViewById(R.id.editStockEditText);

        //OnClickListeners
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleET.getText().toString().isEmpty()) {
                    Toast.makeText(requireActivity(), "Please provide the title of the product...", Toast.LENGTH_LONG).show();
                } else if (numberET.getText().toString().isEmpty()) {
                    Toast.makeText(requireActivity(), "Please provide the quantity of the product...", Toast.LENGTH_LONG).show();
                } else if (buyingPriceET.getText().toString().isEmpty()) {
                    Toast.makeText(requireActivity(), "Please provide the price of the product...", Toast.LENGTH_LONG).show();
                } else {
                    //Setting the data into strings
                    productName = Objects.requireNonNull(titleET.getText()).toString();
                    productBuyingPrice = Objects.requireNonNull(buyingPriceET.getText()).toString();
                    productSellingPrice = Objects.requireNonNull(sellingPriceET.getText()).toString();
                    productQuantity = numberET.getText().toString();

                    final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle(R.string.builderConfirmDetails);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.setMessage(getString(R.string.pdUploading));
                            progressDialog.setTitle(getString(R.string.pdPleaseWait));
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();

                            if (locationFrom.equals(getString(R.string.fragmentNewProduct))) {
                                uploadNewProductToDB();
                            } else if (locationFrom.equals(getString(R.string.fragmentEditProduct))) {
                                uploadEditedProduct();
                            }
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
            }
        });

        //initialValues
        numberET.setText("1");

        //Methods
        numberPicker();
    }

    private void uploadNewProductToDB() {
        HashMap<String, Object> productInfo = new HashMap<>();
        productInfo.put("productTitle", productName);
        productInfo.put("productQuantity", productQuantity);
        productInfo.put("productBuyingPrice", productBuyingPrice);
        productInfo.put("productSellingPrice", productSellingPrice);
        productInfo.put("productKey", newProductKey);

        addDb.child(newProductKey).updateChildren(productInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(requireActivity(), productName + "details uploaded successfully.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireActivity(), StockActivity.class));
                    requireActivity().finish();
                } else {
                    Toast.makeText(requireActivity(), "Failed to upload details for " + productName, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
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

    private void getDataFromDatabase() {
        editDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StockMainObject stockMainObject = snapshot.getValue(StockMainObject.class);
                assert stockMainObject != null;

                titleET.setText(stockMainObject.getProductTitle());
                buyingPriceET.setText(stockMainObject.getProductBuyingPrice());
                sellingPriceET.setText(stockMainObject.getProductSellingPrice());
                numberET.setText(stockMainObject.getProductQuantity());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadEditedProduct() {
        HashMap<String, Object> productInfo = new HashMap<>();
        productInfo.put("productTitle", productName);
        productInfo.put("productQuantity", productQuantity);
        productInfo.put("productBuyingPrice", productBuyingPrice);
        productInfo.put("productSellingPrice", productSellingPrice);

        editDB.updateChildren(productInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(requireActivity(), productName + "details edited successfully.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireActivity(), StockActivity.class));
                    requireActivity().finish();
                } else {
                    Toast.makeText(requireActivity(), "Failed to upload details for " + productName, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}
