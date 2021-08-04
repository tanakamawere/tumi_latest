package org.tmz.tumi.Finances;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.CustomersCartAdapter;
import org.tmz.tumi.Objects.CustomerObject;
import org.tmz.tumi.R;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentCustomer extends Fragment {

    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    ArrayList<CustomerObject> arrayList;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCustomersFrag);
        getCustomersList();
        return view;
    }

    private void getCustomersList() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        arrayList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Tumi Information")
                .child("Customers");

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
                    Toast.makeText(getActivity(), "There was an error. Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        adapter = new CustomersCartAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }
}
