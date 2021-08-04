package org.tmz.tumi.Main.Dashboard;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.CustomersMainAdapter;
import org.tmz.tumi.Objects.CustomerObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CustomersActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    ArrayList<CustomerObject> arrayList;
    DatabaseReference databaseReference;
    Intent intent;

    //manually add customers
    private CardView cardView;
    private TextInputEditText name, phone;
    private Button cancel, addCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        //Getting Intent from saleCartActivity
        intent = getIntent();
        intent.getBooleanExtra("fromCart", true);

        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Tumi Information")
                .child("Customers");

        arrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewCustomers);

        //Initialization of manually adding customer
        cardView = findViewById(R.id.manuallyAddCustomersCardView);
        name = findViewById(R.id.customerNameMainEditText);
        phone = findViewById(R.id.customerPhoneNumberMainEditText);
        cancel = findViewById(R.id.cancelAddCustomerButton);
        addCustomer = findViewById(R.id.confirmAddCustomerButton);

        cardView.setVisibility(View.GONE);

        ExtendedFloatingActionButton fab = findViewById(R.id.fabAddCustomer);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomersActivity.this);
                CharSequence[] options = new CharSequence[]{
                        "Enter manually", "Import from customers"
                };
                builder.setTitle("Add customer");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            cardView.setVisibility(View.VISIBLE);

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cardView.setVisibility(View.GONE);
                                }
                            });

                            addCustomer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!name.getText().toString().equals("")) {
                                        String randomKey = FirebaseDatabase.getInstance().getReference()
                                                .child("Users")
                                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                                .child("Tumi Information")
                                                .push().getKey();

                                        DatabaseReference databaseReference = new FirebaseMethods().getDatabaseReference()
                                                .child("Tumi Information")
                                                .child("Customers")
                                                .child(Objects.requireNonNull(randomKey))
                                                .child("Details");

                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("name", name.getText().toString());
                                        map.put("phoneNumber", phone.getText().toString());
                                        map.put("customerKey", randomKey);

                                        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(CustomersActivity.this, name.getText().toString() + " added successfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(CustomersActivity.this, CustomersActivity.class));
                                                } else {
                                                    Toast.makeText(CustomersActivity.this, name.getText().toString() + " failed to be added", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        } else if (which == 1) {
                            Fragment fragment = new ContactsFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutCustomers, fragment)
                                    .commit();
                        }
                    }
                });
                builder.show();
            }
        });
        getCustomerPermissions();
        getCustomersList();
    }

    private void getCustomerPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }

    private void getCustomersList() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = "",
                            phoneNumber = "",
                            key = "";

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        name = dataSnapshot.child("Details").child("name").getValue().toString();
                        phoneNumber = dataSnapshot.child("Details").child("phoneNumber").getValue().toString();
                        key = dataSnapshot.child("Details").child("customerKey").getValue().toString();


                        CustomerObject customerObject = new CustomerObject(name, phoneNumber, key);
                        arrayList.add(customerObject);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(CustomersActivity.this, "There was an error. Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        adapter = new CustomersMainAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }
}