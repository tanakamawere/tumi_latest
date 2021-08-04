package org.tmz.tumi.Main.Dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Adapters.MoneyAdapter;
import org.tmz.tumi.Objects.MoneyObject;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.util.ArrayList;

public class FragmentMoney extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference databaseReference;
    private ArrayList<MoneyObject> arrayList;
    private ProgressBar progressBar;
    private TextView nullInternet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_money, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewDebts);
        arrayList = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBarDebt);
        nullInternet = view.findViewById(R.id.nullInternetDebtTV);
        nullInternet.setVisibility(View.GONE);

        //Vars
        FirebaseMethods firebaseMethods = new FirebaseMethods();
        databaseReference = firebaseMethods.getDatabaseReference()
                .child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbMoney));

        ExtendedFloatingActionButton actionButton = view.findViewById(R.id.fabRecordDebts);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentAddDebtCredit fragment = new FragmentAddDebtCredit();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out  // popExit
                        ).replace(R.id.frameLayoutDashboard, fragment)
                        .addToBackStack(null).commit();
            }
        });

        //Method calls
        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnap : snapshot.getChildren()) {

                        String name = "", amount = "",
                                phone = "", key = "",
                                date = "", type = "";

                        name = childSnap.child("name").getValue().toString();
                        amount = childSnap.child("amount").getValue().toString();
                        phone = childSnap.child("phone").getValue().toString();
                        key = childSnap.child("key").getValue().toString();
                        date = childSnap.child("date").getValue().toString();
                        type = childSnap.child("type").getValue().toString();

                        MoneyObject object = new MoneyObject(name, phone, amount, date, key, type);
                        arrayList.add(object);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    nullInternet.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new MoneyAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }
}
